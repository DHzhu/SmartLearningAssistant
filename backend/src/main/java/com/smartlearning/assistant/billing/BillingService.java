package com.smartlearning.assistant.billing;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BillingService {

    private static final Logger log = LoggerFactory.getLogger(BillingService.class);
    private static final String QUOTA_KEY_PREFIX = "quota:";

    private final StringRedisTemplate redisTemplate;
    private final UserQuotaRepository quotaRepository;
    private final BillingLogRepository billingLogRepository;
    private final Map<Long, Long> pendingSyncs = new ConcurrentHashMap<>();

    public BillingService(
            StringRedisTemplate redisTemplate,
            UserQuotaRepository quotaRepository,
            BillingLogRepository billingLogRepository) {
        this.redisTemplate = redisTemplate;
        this.quotaRepository = quotaRepository;
        this.billingLogRepository = billingLogRepository;
    }

    /**
     * Initialize user quota in Redis from database.
     */
    public void ensureQuotaInRedis(Long userId) {
        String key = QUOTA_KEY_PREFIX + userId;
        if (Boolean.FALSE.equals(redisTemplate.hasKey(key))) {
            Optional<UserQuota> quota = quotaRepository.findByUserId(userId);
            long balance = quota.map(UserQuota::getBalance).orElse(100000L);
            redisTemplate.opsForValue().set(key, String.valueOf(balance));
        }
    }

    /**
     * Atomic token deduction using Redis Lua script.
     * Returns remaining balance, or -1 if insufficient, -2 if not initialized.
     */
    public long deductTokens(Long userId, long amount) {
        ensureQuotaInRedis(userId);
        String key = QUOTA_KEY_PREFIX + userId;

        String luaScript = """
                local key = KEYS[1]
                local deductAmount = tonumber(ARGV[1])
                local currentBalance = redis.call('GET', key)
                if currentBalance == false then
                    return -2
                end
                currentBalance = tonumber(currentBalance)
                if currentBalance < deductAmount then
                    return -1
                end
                local newBalance = currentBalance - deductAmount
                redis.call('SET', key, tostring(newBalance))
                return newBalance
                """;

        Long result = redisTemplate.execute(
                new org.springframework.data.redis.core.script.DefaultRedisScript<>(luaScript, Long.class),
                java.util.Collections.singletonList(key),
                String.valueOf(amount));

        if (result != null && result >= 0) {
            pendingSyncs.put(userId, result);
            billingLogRepository.save(new BillingLog(userId, amount, "DEDUCT",
                    "Token deduction for AI conversation"));
        }

        return result != null ? result : -2;
    }

    /**
     * Get current balance from Redis (or database fallback).
     */
    public long getBalance(Long userId) {
        String key = QUOTA_KEY_PREFIX + userId;
        String balance = redisTemplate.opsForValue().get(key);
        if (balance != null) {
            return Long.parseLong(balance);
        }

        return quotaRepository.findByUserId(userId)
                .map(UserQuota::getBalance)
                .orElse(0L);
    }

    /**
     * Add tokens (recharge).
     */
    public void addTokens(Long userId, long amount) {
        ensureQuotaInRedis(userId);
        String key = QUOTA_KEY_PREFIX + userId;
        redisTemplate.opsForValue().increment(key, amount);
        pendingSyncs.put(userId, getBalance(userId));
        billingLogRepository.save(new BillingLog(userId, amount, "RECHARGE",
                "Token recharge"));
    }

    /**
     * Scheduled task to sync Redis balances to PostgreSQL.
     */
    @Scheduled(fixedDelay = 300000) // 5 minutes
    @Transactional
    public void syncBalancesToDatabase() {
        if (pendingSyncs.isEmpty()) {
            return;
        }

        log.info("Syncing {} pending balance updates to database", pendingSyncs.size());

        var entries = new java.util.HashMap<>(pendingSyncs);
        pendingSyncs.clear();

        for (var entry : entries.entrySet()) {
            Long userId = entry.getKey();
            Long balance = entry.getValue();

            try {
                UserQuota quota = quotaRepository.findByUserId(userId)
                        .orElseGet(() -> new UserQuota(userId, 0L));
                quota.setBalance(balance);
                quotaRepository.save(quota);
            } catch (Exception e) {
                log.error("Failed to sync balance for user {}: {}", userId, e.getMessage());
                pendingSyncs.put(userId, balance);
            }
        }
    }

    public java.util.List<BillingLog> getBillingHistory(Long userId) {
        return billingLogRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
}
