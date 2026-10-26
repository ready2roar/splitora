package com.splitora.dto.request;

import com.splitora.model.enums.SplitType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateExpenseRequest {
    private String description;
    private BigDecimal amount;
    private Long paidByUserId;
    private Long groupId; // Optional (null for 1-on-1 expenses)
    private SplitType splitType;
    private List<SplitDetailDto> splits; // List of participants and their specific values
}
