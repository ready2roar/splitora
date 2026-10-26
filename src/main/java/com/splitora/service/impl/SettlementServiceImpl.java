package com.splitora.service.impl;

import com.splitora.common.exception.BadRequestException;
import com.splitora.dto.request.CreateSettlementRequest;
import com.splitora.dto.response.SettlementResponse;
import com.splitora.model.entity.Settlement;
import com.splitora.model.entity.User;
import com.splitora.repository.SettlementRepository;
import com.splitora.repository.UserRepository;
import com.splitora.service.SettlementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class SettlementServiceImpl implements SettlementService {

    private final SettlementRepository settlementRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public SettlementResponse createSettlement(CreateSettlementRequest request) {
        log.info("Processing settlement: User {} paying User {} amount: {}",
                request.getPayerId(), request.getPayeeId(), request.getAmount());

        // 1. Validate Amount
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Settlement amount must be greater than zero.");
        }

        // 2. Validate Payer and Payee are different
        if (request.getPayerId().equals(request.getPayeeId())) {
            throw new BadRequestException("Payer and payee cannot be the same user.");
        }

        // 3. Fetch Payer
        User payer = userRepository.findById(request.getPayerId())
                .orElseThrow(() -> new BadRequestException("Payer not found with ID: " + request.getPayerId()));

        // 4. Fetch Payee
        User payee = userRepository.findById(request.getPayeeId())
                .orElseThrow(() -> new BadRequestException("Payee not found with ID: " + request.getPayeeId()));

        // 5. Build and Save Settlement Entity
        Settlement settlement = Settlement.builder()
                .payer(payer)
                .payee(payee)
                .amount(request.getAmount())
                .build();

        Settlement savedSettlement = settlementRepository.save(settlement);
        log.info("Successfully recorded settlement with ID: {}", savedSettlement.getId());

        // 6. Map to Response DTO
        return SettlementResponse.builder()
                .id(savedSettlement.getId())
                .payerId(payer.getId())
                .payerName(payer.getName())
                .payeeId(payee.getId())
                .payeeName(payee.getName())
                .amount(savedSettlement.getAmount())
                .createdAt(savedSettlement.getCreatedAt())
                .build();
    }
}
