package com.splitora.service.engine;

import com.splitora.common.exception.BadRequestException;
import com.splitora.dto.request.CreateExpenseRequest;
import com.splitora.model.entity.Expense;
import com.splitora.model.entity.ExpenseSplit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExpenseSplitEngine {
    private final Map<String, SplitStrategy> strategyMap;

    public List<ExpenseSplit> processSplits(CreateExpenseRequest request, Expense expense) {
        String strategyKey = request.getSplitType().name();
        SplitStrategy strategy = strategyMap.get(strategyKey);

        if (strategy == null) {
            throw new BadRequestException("Invalid split type provided: " + request.getSplitType());
        }

        return strategy.calculateSplits(expense, request.getAmount(), request.getSplits());
    }
}
