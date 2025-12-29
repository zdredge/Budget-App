package com.budget.dto;

import java.math.BigDecimal;

public record CategorySummaryDTO(
    Long categoryId,
    String categoryName,
    String categoryColor,
    BigDecimal spent,
    BigDecimal limit,
    double percentUsed,
    String status // "ok", "warning", "exceeded"
) {}

