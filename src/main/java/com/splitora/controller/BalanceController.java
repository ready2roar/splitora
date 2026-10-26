package com.splitora.controller;

import com.splitora.dto.response.UserBalanceSummary;
import com.splitora.service.BalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/balances")
@RequiredArgsConstructor
public class BalanceController {
    private final BalanceService balanceService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<UserBalanceSummary> getUserBalanceSummary(@PathVariable Long userId) {
        UserBalanceSummary summary = balanceService.getUserBalanceSummary(userId);
        return ResponseEntity.ok(summary);
    }
}
