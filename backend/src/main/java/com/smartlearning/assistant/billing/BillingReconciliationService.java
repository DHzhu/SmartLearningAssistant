package com.smartlearning.assistant.billing;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BillingReconciliationService {

    private static final Logger log = LoggerFactory.getLogger(BillingReconciliationService.class);
    private static final String QUOTA_KEY_PREFIX = "user:quota:";

    private final UserQuotaRepository quotaRepository;
    private final BillingLogRepository logRepository;
    private final StringRedisTemplate redisTemplate;

    public BillingReconciliationService(
            UserQuotaRepository quotaRepository,
            BillingLogRepository logRepository,
            StringRedisTemplate redisTemplate) {
        this.quotaRepository = quotaRepository;
        this.logRepository = logRepository;
        this.redisTemplate = redisTemplate;
    }

    public record ReconciliationResult(
            Long userId,
            long dbBalance,
            long redisBalance,
            boolean isConsistent,
            String actionTaken) {}

    @Transactional
    public ReconciliationResult reconcileUser(Long userId) {
        UserQuota quota = quotaRepository.findByUserId(userId).orElse(null);
        if (quota == null) {
            return new ReconciliationResult(userId, 0, 0, true, "USER_NOT_FOUND");
        }

        long dbBalance = quota.getBalance();
        String cachedVal = redisTemplate.opsForValue().get(QUOTA_KEY_PREFIX + userId);
        long redisBalance = cachedVal != null ? Long.parseLong(cachedVal) : dbBalance;

        if (cachedVal == null) {
            redisTemplate.opsForValue().set(QUOTA_KEY_PREFIX + userId, String.valueOf(dbBalance));
            return new ReconciliationResult(userId, dbBalance, dbBalance, true, "REDIS_CACHE_WARMED");
        }

        if (dbBalance != redisBalance) {
            log.warn("Reconciliation mismatch for user {}: DB={}, Redis={}. Syncing Redis -> DB",
                    userId, dbBalance, redisBalance);
            quota.setBalance(redisBalance);
            quotaRepository.save(quota);
            return new ReconciliationResult(userId, dbBalance, redisBalance, false, "SYNCED_REDIS_TO_DB");
        }

        return new ReconciliationResult(userId, dbBalance, redisBalance, true, "IN_SYNC");
    }
}
