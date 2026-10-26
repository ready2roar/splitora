package com.splitora.dto.response;

import com.splitora.model.enums.SplitType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ExpenseResponse {

    private Long id;
    private String description;
    private BigDecimal amount;
    private Long paidByUserId;
    private String paidByName;
    private Long groupId;
    private SplitType splitType;
    private LocalDateTime createdAt;
    private List<SplitResponse> splits;
}
