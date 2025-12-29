package com.budget.dto;

import java.math.BigDecimal;

public record CategoryDTO(
    Long id,
    String name,
    BigDecimal monthlyLimit,
    String color,
    String description
) {}
