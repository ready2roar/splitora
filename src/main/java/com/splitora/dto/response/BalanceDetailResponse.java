package com.splitora.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class BalanceDetailResponse {

    private Long userId;
    private String userName;
    private BigDecimal amount; // Positive means they owe you, Negative means you owe them
}
