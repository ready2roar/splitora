package com.splitora.dto.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SplitDetailDto {
    private Long userId;
    private BigDecimal value; // Used for EXACT amount or PERCENTAGE share. Ignored for EQUAL splits.
}
