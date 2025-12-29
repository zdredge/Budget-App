package com.budget.dto;

import java.math.BigDecimal;

public record CreateExpenseRequest(
    BigDecimal amount,
    String description,
    String date,
    Long categoryId
) {}

