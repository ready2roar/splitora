package com.splitora.service;

import com.splitora.dto.request.CreateExpenseRequest;
import com.splitora.dto.response.ExpenseResponse;
public interface ExpenseService {
    ExpenseResponse createExpense(CreateExpenseRequest request);
}
