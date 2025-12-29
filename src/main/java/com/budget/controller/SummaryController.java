package com.budget.controller;

import com.budget.dto.CategorySummaryDTO;
import com.budget.dto.MonthlySummaryDTO;
import com.budget.model.Category;
import com.budget.model.Expense;
import com.budget.repository.CategoryRepository;
import com.budget.repository.ExpenseRepository;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.List;

/**
 * REST controller for generating budget summary reports.
 */
@RestController
@RequestMapping("/api/summary")
public class SummaryController {

    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;

    public SummaryController(ExpenseRepository expenseRepository, CategoryRepository categoryRepository) {
        this.expenseRepository = expenseRepository;
        this.categoryRepository = categoryRepository;
    }

    /**
     * Returns a monthly summary with total spending and per-category breakdown.
     * @param month optional month in "YYYY-MM" format; defaults to current month
     */
    @GetMapping
    public MonthlySummaryDTO getMonthlySummary(@RequestParam(required = false) String month) {
        YearMonth yearMonth = month != null ? YearMonth.parse(month) : YearMonth.now();
        int year = yearMonth.getYear();
        int monthValue = yearMonth.getMonthValue();

        List<Category> categories = categoryRepository.findAll();
        List<Expense> expenses = expenseRepository.findByMonth(year, monthValue);

        BigDecimal totalSpent = expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalLimit = categories.stream()
                .map(Category::getMonthlyLimit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<CategorySummaryDTO> categoryBreakdown = categories.stream()
                .map(category -> {
                    BigDecimal spent = expenses.stream()
                            .filter(e -> e.getCategory().getId().equals(category.getId()))
                            .map(Expense::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal limit = category.getMonthlyLimit();
                    double percentUsed = calculatePercentage(spent, limit);
                    String status = determineStatus(percentUsed, limit);

                    return new CategorySummaryDTO(
                            category.getId(),
                            category.getName(),
                            category.getColor(),
                            spent,
                            limit,
                            percentUsed,
                            status
                    );
                })
                .toList();

        return new MonthlySummaryDTO(year, monthValue, totalSpent, totalLimit, categoryBreakdown);
    }

    /**
     * Calculates the percentage of limit used.
     * @return 0.0 if limit is null or zero, otherwise (spent/limit)*100
     */
    private double calculatePercentage(BigDecimal spent, BigDecimal limit) {
        if (limit == null || limit.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        return spent.multiply(BigDecimal.valueOf(100))
                .divide(limit, 2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    /**
     * Determines budget status based on percentage used.
     * @return "exceeded" if >100%, "warning" if >=80%, otherwise "ok"
     */
    private String determineStatus(double percentUsed, BigDecimal limit) {
        if (limit == null || limit.compareTo(BigDecimal.ZERO) == 0) {
            return "ok";
        }
        if (percentUsed > 100) {
            return "exceeded";
        } else if (percentUsed >= 80) {
            return "warning";
        }
        return "ok";
    }
}

