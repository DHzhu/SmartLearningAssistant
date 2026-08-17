package com.smartlearning.assistant.agent.tools;

import com.smartlearning.assistant.billing.BillingService;
import org.springframework.stereotype.Component;

@Component
public class AccountTool {

    private final BillingService billingService;

    public AccountTool(BillingService billingService) {
        this.billingService = billingService;
    }

    public record BalanceResponse(Long userId, long balance, String message) {}

    public BalanceResponse getUserBalance(Long userId) {
        long balance = billingService.getBalance(userId);
        return new BalanceResponse(userId, balance, "用户 " + userId + " 的当前 Token 余额为: " + balance);
    }
}
