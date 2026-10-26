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

@Component("EQUAL")
@RequiredArgsConstructor
public class EqualSplitStrategy implements SplitStrategy{

    private final UserRepository userRepository;

    @Override
    public List<ExpenseSplit> calculateSplits(Expense expense, BigDecimal totalAmount, List<SplitDetailDto> splitDetails) {
        if (splitDetails == null || splitDetails.isEmpty()) {
            throw new BadRequestException("Participants list cannot be empty for an equal split.");
        }

        int totalParticipants = splitDetails.size();
        // Divide amount with 2 scale rounding (Half-Up)
        BigDecimal shareAmount = totalAmount.divide(BigDecimal.valueOf(totalParticipants), 2, RoundingMode.DOWN);

        // Calculate any remainder pennies due to rounding truncation
        BigDecimal totalDistributed = shareAmount.multiply(BigDecimal.valueOf(totalParticipants));
        BigDecimal remainder = totalAmount.subtract(totalDistributed);

        List<ExpenseSplit> expenseSplits = new ArrayList<>();

        for (int i = 0; i < totalParticipants; i++) {
            SplitDetailDto detail = splitDetails.get(i);
            User user = userRepository.findById(detail.getUserId())
                    .orElseThrow(() -> new BadRequestException("User not found with ID: " + detail.getUserId()));

            BigDecimal currentShare = shareAmount;
            // Distribute the remainder penny to the first participant(s)
            if (i == 0 && remainder.compareTo(BigDecimal.ZERO) > 0) {
                currentShare = currentShare.add(remainder);
            }

            ExpenseSplit split = ExpenseSplit.builder()
                    .expense(expense)
                    .user(user)
                    .amountOwed(currentShare)
                    .build();

            expenseSplits.add(split);
        }

        return expenseSplits;
    }
}