package com.splitora.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class UserBalanceSummary {

    private Long userId;
    private String userName;
    private BigDecimal netBalance; // Total net (Positive = overall owed money, Negative = overall you owe)
    private List<BalanceDetailResponse> breakdown; // Detailed breakdown with specific users
}