package com.splitora.service.engine.strategy;

import com.splitora.common.exception.BadRequestException;
import com.splitora.dto.request.SplitDetailDto;
import com.splitora.model.entity.Expense;
import com.splitora.model.entity.ExpenseSplit;
import com.splitora.model.entity.User;
import com.splitora.repository.UserRepository;
import com.splitora.service.engine.SplitStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Component("PERCENTAGE")
@RequiredArgsConstructor
public class PercentageSplitStrategy implements SplitStrategy {

    private final UserRepository userRepository;

    @Override
    public List<ExpenseSplit> calculateSplits(Expense expense, BigDecimal totalAmount, List<SplitDetailDto> splitDetails) {
        if (splitDetails == null || splitDetails.isEmpty()) {
            throw new BadRequestException("Participants list cannot be empty for a percentage split.");
        }

        BigDecimal totalPercentage = splitDetails.stream()
                .map(SplitDetailDto::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalPercentage.compareTo(BigDecimal.valueOf(100)) != 0) {
            throw new BadRequestException("Sum of percentages must equal 100%. Current sum: " + totalPercentage);
        }

        List<ExpenseSplit> expenseSplits = new ArrayList<>();
        BigDecimal allocatedSum = BigDecimal.ZERO;

        for (int i = 0; i < splitDetails.size(); i++) {
            SplitDetailDto detail = splitDetails.get(i);
            User user = userRepository.findById(detail.getUserId())
                    .orElseThrow(() -> new BadRequestException("User not found with ID: " + detail.getUserId()));

            BigDecimal shareAmount;
            if (i == splitDetails.size() - 1) {
                // Last person absorbs any rounding error to match total amount precisely
                shareAmount = totalAmount.subtract(allocatedSum);
            } else {
                shareAmount = totalAmount.multiply(detail.getValue())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                allocatedSum = allocatedSum.add(shareAmount);
            }

            ExpenseSplit split = ExpenseSplit.builder()
                    .expense(expense)
                    .user(user)
                    .amountOwed(shareAmount)
                    .build();

            expenseSplits.add(split);
        }

        return expenseSplits;
    }
}
