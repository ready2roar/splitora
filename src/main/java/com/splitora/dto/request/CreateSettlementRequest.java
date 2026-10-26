package com.splitora.dto.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreateSettlementRequest {
    private Long payerId;  // User sending the money
    private Long payeeId;  // User receiving the money
    private BigDecimal amount;
}
