package com.splitora.service.impl;

import com.splitora.common.exception.BadRequestException;
import com.splitora.dto.request.CreateExpenseRequest;
import com.splitora.dto.response.ExpenseResponse;
import com.splitora.dto.response.SplitResponse;
import com.splitora.model.entity.Expense;
import com.splitora.model.entity.ExpenseSplit;
import com.splitora.model.entity.Group;
import com.splitora.model.entity.User;
import com.splitora.repository.ExpenseRepository;
import com.splitora.repository.GroupRepository;
import com.splitora.repository.UserRepository;
import com.splitora.service.ExpenseService;
import com.splitora.service.engine.ExpenseSplitEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService{
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final ExpenseSplitEngine expenseSplitEngine;

    @Override
    @Transactional
    public ExpenseResponse createExpense(CreateExpenseRequest request) {
        log.info("Received request to create expense: '{}' for amount: {} with split type: {}",
                request.getDescription(), request.getAmount(), request.getSplitType());

        // 1. Fetch Payer
        User payer = userRepository.findById(request.getPaidByUserId())
                .orElseThrow(() -> {
                    log.error("Payer not found with ID: {}", request.getPaidByUserId());
                    return new BadRequestException("Payer not found with ID: " + request.getPaidByUserId());
                });

        // 2. Fetch Group (if provided)
        Group group = null;
        if (request.getGroupId() != null) {
            group = groupRepository.findById(request.getGroupId())
                    .orElseThrow(() -> {
                        log.error("Group not found with ID: {}", request.getGroupId());
                        return new BadRequestException("Group not found with ID: " + request.getGroupId());
                    });
            log.debug("Expense associated with group ID: {}", group.getId());
        } else {
            log.debug("Expense is a 1-on-1 direct expense (no group)");
        }

        // 3. Build Expense Entity
        Expense expense = Expense.builder()
                .description(request.getDescription())
                .amount(request.getAmount())
                .paidBy(payer)
                .group(group)
                .splitType(request.getSplitType())
                .build();

        // 4. Run through the Split Engine Strategy Pattern
        log.debug("Executing split strategy for type: {}", request.getSplitType());
        List<ExpenseSplit> calculatedSplits = expenseSplitEngine.processSplits(request, expense);
        expense.setSplits(calculatedSplits);

        // 5. Save to Database
        Expense savedExpense = expenseRepository.save(expense);
        log.info("Successfully saved expense with ID: {} and generated {} split shares",
                savedExpense.getId(), savedExpense.getSplits().size());

        // 6. Map to Response DTO
        return mapToResponse(savedExpense);
    }

    private ExpenseResponse mapToResponse(Expense expense) {
        List<SplitResponse> splitResponses = expense.getSplits().stream()
                .map(split -> SplitResponse.builder()
                        .userId(split.getUser().getId())
                        .userName(split.getUser().getName())
                        .amountOwed(split.getAmountOwed())
                        .build())
                .collect(Collectors.toList());

        return ExpenseResponse.builder()
                .id(expense.getId())
                .description(expense.getDescription())
                .amount(expense.getAmount())
                .paidByUserId(expense.getPaidBy().getId())
                .paidByName(expense.getPaidBy().getName())
                .groupId(expense.getGroup() != null ? expense.getGroup().getId() : null)
                .splitType(expense.getSplitType())
                .createdAt(expense.getCreatedAt())
                .splits(splitResponses)
                .build();

    }


}
