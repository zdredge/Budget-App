package com.budget.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseDTO(
    Long id,
    BigDecimal amount,
    String description,
    LocalDate date,
    Long categoryId,
    String categoryName,
    String categoryColor
) {}

