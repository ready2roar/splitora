package com.splitora.service;

import com.splitora.dto.response.UserBalanceSummary;

public interface BalanceService {
    UserBalanceSummary getUserBalanceSummary(Long userId);
}
