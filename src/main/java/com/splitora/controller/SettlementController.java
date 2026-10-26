package com.splitora.controller;

import com.splitora.dto.request.CreateSettlementRequest;
import com.splitora.dto.response.SettlementResponse;
import com.splitora.service.SettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settlements")
@RequiredArgsConstructor
public class SettlementController {

    private final SettlementService settlementService;

    @PostMapping
    public ResponseEntity<SettlementResponse> createSettlement(@RequestBody CreateSettlementRequest request) {
        SettlementResponse response = settlementService.createSettlement(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
