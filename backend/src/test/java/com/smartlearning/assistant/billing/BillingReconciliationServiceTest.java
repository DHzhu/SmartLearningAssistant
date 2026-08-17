package com.smartlearning.assistant.billing;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(MockitoExtension.class)
class BillingReconciliationServiceTest {

    @Mock
    private UserQuotaRepository quotaRepository;
    @Mock
    private BillingLogRepository logRepository;
    @Mock
    private StringRedisTemplate redisTemplate;
    @Mock
    private ValueOperations<String, String> valueOperations;

    private BillingReconciliationService reconciliationService;

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        reconciliationService = new BillingReconciliationService(quotaRepository, logRepository, redisTemplate);
    }

    @Test
    void shouldReturnInSyncWhenDbAndRedisMatch() {
        UserQuota quota = new UserQuota(1L, 95000L);
        when(quotaRepository.findByUserId(1L)).thenReturn(Optional.of(quota));
        when(valueOperations.get("user:quota:1")).thenReturn("95000");

        BillingReconciliationService.ReconciliationResult result = reconciliationService.reconcileUser(1L);

        assertTrue(result.isConsistent());
        assertEquals("IN_SYNC", result.actionTaken());
    }

    @Test
    void shouldSyncRedisToDbWhenDiscrepancyDetected() {
        UserQuota quota = new UserQuota(1L, 100000L);
        when(quotaRepository.findByUserId(1L)).thenReturn(Optional.of(quota));
        when(valueOperations.get("user:quota:1")).thenReturn("90000");

        BillingReconciliationService.ReconciliationResult result = reconciliationService.reconcileUser(1L);

        assertFalse(result.isConsistent());
        assertEquals("SYNCED_REDIS_TO_DB", result.actionTaken());
        assertEquals(90000L, quota.getBalance());
        verify(quotaRepository).save(quota);
    }
}
