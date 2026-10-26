package com.splitora.service.impl;

import com.splitora.common.exception.BadRequestException;
import com.splitora.dto.response.BalanceDetailResponse;
import com.splitora.dto.response.UserBalanceSummary;
import com.splitora.model.entity.ExpenseSplit;
import com.splitora.model.entity.Settlement;
import com.splitora.model.entity.User;
import com.splitora.repository.ExpenseSplitRepository;
import com.splitora.repository.SettlementRepository;
import com.splitora.repository.UserRepository;
import com.splitora.service.BalanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {

    private final UserRepository userRepository;
    private final ExpenseSplitRepository expenseSplitRepository;
    private final SettlementRepository settlementRepository;

    @Override
    public UserBalanceSummary getUserBalanceSummary(Long userId) {
        log.info("Calculating balance summary for user ID: {}", userId);

        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found with ID: " + userId));

        // Map to keep track of net amounts owed between targetUser and other users
        // Key: Other UserId, Value: Net amount (Positive = other user owes targetUser, Negative = targetUser owes other user)
        Map<Long, BigDecimal> netBalances = new HashMap<>();

        // 1. Process Expenses where targetUser paid (Others owe targetUser)
        // Find all expense splits where the expense was paid by targetUser
        List<ExpenseSplit> splitsForExpensesPaidByUser = expenseSplitRepository.findAll().stream()
                .filter(split -> split.getExpense().getPaidBy().getId().equals(userId))
                .toList();

        for (ExpenseSplit split : splitsForExpensesPaidByUser) {
            Long debtorId = split.getUser().getId();
            // Don't count what the user owes themselves
            if (debtorId.equals(userId)) continue;

            netBalances.put(debtorId, netBalances.getOrDefault(debtorId, BigDecimal.ZERO).add(split.getAmountOwed()));
        }

        // 2. Process Expenses where targetUser owes money to others
        List<ExpenseSplit> splitsOwedByUser = expenseSplitRepository.findByUserId(userId);
        for (ExpenseSplit split : splitsOwedByUser) {
            Long creditorId = split.getExpense().getPaidBy().getId();
            // Don't count expenses paid by user themselves
            if (creditorId.equals(userId)) continue;

            netBalances.put(creditorId, netBalances.getOrDefault(creditorId, BigDecimal.ZERO).subtract(split.getAmountOwed()));
        }

        // 3. Process Settlements
        List<Settlement> allSettlements = settlementRepository.findAllSettlementsForUser(userId);
        for (Settlement settlement : allSettlements) {
            if (settlement.getPayer().getId().equals(userId)) {
                // targetUser paid someone else, reducing what targetUser owes them
                Long payeeId = settlement.getPayee().getId();
                netBalances.put(payeeId, netBalances.getOrDefault(payeeId, BigDecimal.ZERO).add(settlement.getAmount()));
            } else if (settlement.getPayee().getId().equals(userId)) {
                // targetUser received money from someone else, reducing what they owe targetUser
                Long payerId = settlement.getPayer().getId();
                netBalances.put(payerId, netBalances.getOrDefault(payerId, BigDecimal.ZERO).subtract(settlement.getAmount()));
            }
        }

        // 4. Build Response DTOs
        List<BalanceDetailResponse> breakdown = new ArrayList<>();
        BigDecimal totalNetBalance = BigDecimal.ZERO;

        for (Map.Entry<Long, BigDecimal> entry : netBalances.entrySet()) {
            Long otherUserId = entry.getKey();
            BigDecimal amount = entry.getValue();

            // Skip zero balances
            if (amount.compareTo(BigDecimal.ZERO) == 0) continue;

            User otherUser = userRepository.findById(otherUserId).orElse(null);
            if (otherUser == null) continue;

            breakdown.add(BalanceDetailResponse.builder()
                    .userId(otherUserId)
                    .userName(otherUser.getName())
                    .amount(amount)
                    .build());

            totalNetBalance = totalNetBalance.add(amount);
        }

        log.info("Calculated balance summary for user {}. Net balance: {}", targetUser.getName(), totalNetBalance);

        return UserBalanceSummary.builder()
                .userId(targetUser.getId())
                .userName(targetUser.getName())
                .netBalance(totalNetBalance)
                .breakdown(breakdown)
                .build();
    }
}
