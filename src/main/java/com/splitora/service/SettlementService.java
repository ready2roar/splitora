package com.splitora.service;

import com.splitora.dto.request.CreateSettlementRequest;
import com.splitora.dto.response.SettlementResponse;
public interface SettlementService {
    SettlementResponse createSettlement(CreateSettlementRequest request);
}
