package com.splitora.service.engine;

import com.splitora.dto.request.SplitDetailDto;
import com.splitora.model.entity.Expense;
import com.splitora.model.entity.ExpenseSplit;

import java.math.BigDecimal;
import java.util.List;
public interface SplitStrategy {
    List<ExpenseSplit> calculateSplits(Expense expense, BigDecimal totalAmount, List<SplitDetailDto> splitDetails);
}
