package com.smartlearning.assistant.billing;

import com.smartlearning.assistant.auth.UserPrincipal;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/billing")
public class BillingController {

    private final BillingService billingService;

    public BillingController(BillingService billingService) {
        this.billingService = billingService;
    }

    @GetMapping("/balance")
    public ResponseEntity<Map<String, Long>> getBalance(
            @AuthenticationPrincipal UserPrincipal user) {
        long balance = billingService.getBalance(user.userId());
        return ResponseEntity.ok(Map.of("balance", balance));
    }

    @PostMapping("/recharge")
    public ResponseEntity<Map<String, Long>> recharge(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam long amount) {
        billingService.addTokens(user.userId(), amount);
        long balance = billingService.getBalance(user.userId());
        return ResponseEntity.ok(Map.of("balance", balance));
    }

    @GetMapping("/history")
    public ResponseEntity<List<BillingLog>> getHistory(
            @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(billingService.getBillingHistory(user.userId()));
    }
}
