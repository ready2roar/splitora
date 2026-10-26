package com.splitora.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class SplitResponse {
    private Long userId;
    private String userName;
    private BigDecimal amountOwed;
}
