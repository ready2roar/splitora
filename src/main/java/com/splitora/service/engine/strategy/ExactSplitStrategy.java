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
import java.util.ArrayList;
import java.util.List;

@Component("EXACT")
@RequiredArgsConstructor
public class ExactSplitStrategy implements SplitStrategy {

        private final UserRepository userRepository;

        @Override
        public List<ExpenseSplit> calculateSplits(Expense expense, BigDecimal totalAmount, List<SplitDetailDto> splitDetails) {
            if (splitDetails == null || splitDetails.isEmpty()) {
                throw new BadRequestException("Participants list cannot be empty for an exact split.");
            }

            BigDecimal sumOfExactAmounts = splitDetails.stream()
                    .map(SplitDetailDto::getValue)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Validate that exact shares add up precisely to the total expense amount
            if (sumOfExactAmounts.compareTo(totalAmount) != 0) {
                throw new BadRequestException("Sum of exact split amounts (" + sumOfExactAmounts +
                        ") does not match total expense amount (" + totalAmount + ").");
            }

            List<ExpenseSplit> expenseSplits = new ArrayList<>();
            for (SplitDetailDto detail : splitDetails) {
                User user = userRepository.findById(detail.getUserId())
                        .orElseThrow(() -> new BadRequestException("User not found with ID: " + detail.getUserId()));

                ExpenseSplit split = ExpenseSplit.builder()
                        .expense(expense)
                        .user(user)
                        .amountOwed(detail.getValue())
                        .build();

                expenseSplits.add(split);
            }

            return expenseSplits;
        }
    }
