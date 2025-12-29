package com.budget.dto;

import java.math.BigDecimal;
import java.util.List;

public record MonthlySummaryDTO(
    int year,
    int month,
    BigDecimal totalSpent,
    BigDecimal totalLimit,
    List<CategorySummaryDTO> categoryBreakdown
) {}

