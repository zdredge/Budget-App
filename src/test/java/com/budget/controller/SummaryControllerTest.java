package com.budget.controller;

import com.budget.model.Category;
import com.budget.model.Expense;
import com.budget.repository.CategoryRepository;
import com.budget.repository.ExpenseRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for SummaryController REST endpoints.
 */
@SpringBootTest
@AutoConfigureMockMvc
class SummaryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category groceries;
    private Category rent;
    private Category utilities;

    @BeforeAll
    static void beforeAll() {
        System.out.println("=== Running SummaryControllerTest ===");
    }

    @BeforeEach
    void setUp() {
        expenseRepository.deleteAll();
        categoryRepository.deleteAll();

        groceries = new Category("Groceries", "#22c55e", new BigDecimal("500.00"), "");
        groceries = categoryRepository.save(groceries);

        rent = new Category("Rent", "#3b82f6", new BigDecimal("2000.00"), "");
        rent = categoryRepository.save(rent);

        utilities = new Category("Utilities", "#f59e0b", new BigDecimal("200.00"), "");
        utilities = categoryRepository.save(utilities);
    }

    /**
     * Tests that zeros are returned when no expenses exist.
     */
    @Test
    void summaryEmpty() throws Exception {
        System.out.println("--- Now testing summary with no expenses ---");

        mockMvc.perform(get("/api/summary").param("month", "2024-12"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.year", is(2024)))
                .andExpect(jsonPath("$.month", is(12)))
                .andExpect(jsonPath("$.totalSpent", is(0)))
                .andExpect(jsonPath("$.totalLimit", is(2700.00)))
                .andExpect(jsonPath("$.categoryBreakdown", hasSize(3)));
    }

    /**
     * Tests that totals are calculated correctly when expenses exist.
     */
    @Test
    void summaryWithExpenses() throws Exception {
        System.out.println("--- Now testing summary total calculation ---");

        expenseRepository.save(new Expense(
                new BigDecimal("150.00"),
                "Weekly groceries",
                LocalDate.of(2024, 12, 10),
                groceries
        ));
        expenseRepository.save(new Expense(
                new BigDecimal("2000.00"),
                "Monthly rent",
                LocalDate.of(2024, 12, 1),
                rent
        ));

        mockMvc.perform(get("/api/summary").param("month", "2024-12"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalSpent", is(2150.00)))
                .andExpect(jsonPath("$.totalLimit", is(2700.00)));
    }

    /**
     * Tests that category breakdown shows correct spending and percentages.
     */
    @Test
    void categoryBreakdown() throws Exception {
        System.out.println("--- Now testing category breakdown calculation ---");

        expenseRepository.save(new Expense(
                new BigDecimal("400.00"),
                "Groceries",
                LocalDate.of(2024, 12, 15),
                groceries
        ));

        mockMvc.perform(get("/api/summary").param("month", "2024-12"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryBreakdown[?(@.categoryName == 'Groceries')].spent", contains(400.00)))
                .andExpect(jsonPath("$.categoryBreakdown[?(@.categoryName == 'Groceries')].limit", contains(500.00)))
                .andExpect(jsonPath("$.categoryBreakdown[?(@.categoryName == 'Groceries')].percentUsed", contains(80.00)));
    }

    /**
     * Tests that 'ok' status is returned when under limit.
     */
    @Test
    void statusOk() throws Exception {
        System.out.println("--- Now testing 'ok' status when under limit ---");

        expenseRepository.save(new Expense(
                new BigDecimal("100.00"),
                "Small purchase",
                LocalDate.of(2024, 12, 15),
                groceries
        ));

        mockMvc.perform(get("/api/summary").param("month", "2024-12"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryBreakdown[?(@.categoryName == 'Groceries')].status", contains("ok")));
    }

    /**
     * Tests that 'warning' status is returned when approaching limit (>=80%).
     */
    @Test
    void statusWarning() throws Exception {
        System.out.println("--- Now testing 'warning' status when approaching limit ---");

        expenseRepository.save(new Expense(
                new BigDecimal("450.00"),
                "Big grocery run",
                LocalDate.of(2024, 12, 15),
                groceries
        ));

        mockMvc.perform(get("/api/summary").param("month", "2024-12"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryBreakdown[?(@.categoryName == 'Groceries')].status", contains("warning")));
    }

    /**
     * Tests that 'warning' status is returned when at exactly 100% of limit.
     */
    @Test
    void statusAtExactLimit() throws Exception {
        System.out.println("--- Now testing 'warning' status when at exactly 100% limit ---");

        expenseRepository.save(new Expense(
                new BigDecimal("500.00"),
                "Exact budget groceries",
                LocalDate.of(2024, 12, 15),
                groceries
        ));

        mockMvc.perform(get("/api/summary").param("month", "2024-12"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryBreakdown[?(@.categoryName == 'Groceries')].percentUsed", contains(100.00)))
                .andExpect(jsonPath("$.categoryBreakdown[?(@.categoryName == 'Groceries')].status", contains("warning")));
    }

    /**
     * Tests that 'exceeded' status is returned when over limit (>100%).
     */
    @Test
    void statusExceeded() throws Exception {
        System.out.println("--- Now testing 'exceeded' status when over limit ---");

        expenseRepository.save(new Expense(
                new BigDecimal("550.00"),
                "Over budget groceries",
                LocalDate.of(2024, 12, 15),
                groceries
        ));

        mockMvc.perform(get("/api/summary").param("month", "2024-12"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryBreakdown[?(@.categoryName == 'Groceries')].status", contains("exceeded")));
    }

    /**
     * Tests that only expenses from the requested month are included.
     */
    @Test
    void filterByMonth() throws Exception {
        System.out.println("--- Now testing expense filtering by month ---");

        expenseRepository.save(new Expense(
                new BigDecimal("100.00"),
                "December expense",
                LocalDate.of(2024, 12, 15),
                groceries
        ));
        expenseRepository.save(new Expense(
                new BigDecimal("200.00"),
                "November expense",
                LocalDate.of(2024, 11, 15),
                groceries
        ));

        mockMvc.perform(get("/api/summary").param("month", "2024-12"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalSpent", is(100.00)));
    }

    /**
     * Tests that current month is used when no month parameter is provided.
     */
    @Test
    void defaultsToCurrentMonth() throws Exception {
        System.out.println("--- Now testing default to current month ---");

        mockMvc.perform(get("/api/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.year").exists())
                .andExpect(jsonPath("$.month").exists());
    }

    /**
     * Tests that 'ok' status is returned when no limit is set.
     */
    @Test
    void noLimitSetReturnsOk() throws Exception {
        System.out.println("--- Now testing 'ok' status when no limit is set ---");

        Category noLimit = new Category("No Limit", "#000000", BigDecimal.ZERO, "");
        noLimit = categoryRepository.save(noLimit);

        expenseRepository.save(new Expense(
                new BigDecimal("1000.00"),
                "Big expense",
                LocalDate.of(2024, 12, 15),
                noLimit
        ));

        mockMvc.perform(get("/api/summary").param("month", "2024-12"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryBreakdown[?(@.categoryName == 'No Limit')].status", contains("ok")));
    }
}
