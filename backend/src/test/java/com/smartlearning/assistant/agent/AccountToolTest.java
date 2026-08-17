package com.smartlearning.assistant.agent;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.smartlearning.assistant.agent.tools.AccountTool;
import com.smartlearning.assistant.billing.BillingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountToolTest {

    @Mock
    private BillingService billingService;

    private AccountTool accountTool;

    @BeforeEach
    void setUp() {
        accountTool = new AccountTool(billingService);
    }

    @Test
    void shouldReturnUserBalance() {
        when(billingService.getBalance(1L)).thenReturn(50000L);

        AccountTool.BalanceResponse response = accountTool.getUserBalance(1L);

        assertNotNull(response);
        assertEquals(1L, response.userId());
        assertEquals(50000L, response.balance());
        assertTrue(response.message().contains("50000"));
    }

    @Test
    void shouldReturnZeroWhenNoBalance() {
        when(billingService.getBalance(2L)).thenReturn(0L);

        AccountTool.BalanceResponse response = accountTool.getUserBalance(2L);

        assertEquals(0L, response.balance());
    }
}
