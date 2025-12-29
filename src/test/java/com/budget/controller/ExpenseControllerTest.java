package com.budget.controller;

import com.budget.dto.CreateExpenseRequest;
import com.budget.model.Category;
import com.budget.model.Expense;
import com.budget.repository.CategoryRepository;
import com.budget.repository.ExpenseRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for ExpenseController REST endpoints.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Category testCategory;

    @BeforeAll
    static void beforeAll() {
        System.out.println("=== Running ExpenseControllerTest ===");
    }

    @BeforeEach
    void setUp() {
        expenseRepository.deleteAll();
        categoryRepository.deleteAll();

        testCategory = new Category("Groceries", "#22c55e", BigDecimal.ZERO, "");
        testCategory = categoryRepository.save(testCategory);
    }

    /**
     * Tests that an empty list is returned when no expenses exist.
     */
    @Test
    void getAllEmpty() throws Exception {
        System.out.println("--- Now testing for no present expenses ---");

        mockMvc.perform(get("/api/expenses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    /**
     * Tests that all expenses are returned when they exist.
     */
    @Test
    void getAllWithData() throws Exception {
        System.out.println("--- Now testing retrieval of all expenses ---");

        Expense expense = new Expense(
                new BigDecimal("50.00"),
                "Weekly groceries",
                LocalDate.of(2024, 12, 15),
                testCategory
        );
        expenseRepository.save(expense);

        mockMvc.perform(get("/api/expenses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].amount", is(50.00)))
                .andExpect(jsonPath("$[0].description", is("Weekly groceries")))
                .andExpect(jsonPath("$[0].categoryName", is("Groceries")));
    }

    /**
     * Tests that expenses are filtered by month.
     */
    @Test
    void getByMonth() throws Exception {
        System.out.println("--- Now testing expense filtering by month ---");

        Expense dec2024 = new Expense(
                new BigDecimal("100.00"),
                "December expense",
                LocalDate.of(2024, 12, 15),
                testCategory
        );
        Expense nov2024 = new Expense(
                new BigDecimal("75.00"),
                "November expense",
                LocalDate.of(2024, 11, 10),
                testCategory
        );
        expenseRepository.save(dec2024);
        expenseRepository.save(nov2024);

        mockMvc.perform(get("/api/expenses").param("month", "2024-12"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].description", is("December expense")));
    }

    /**
     * Tests that a single expense is returned by ID.
     */
    @Test
    void getByIdExists() throws Exception {
        System.out.println("--- Now testing retrieval of expense by ID ---");

        Expense expense = new Expense(
                new BigDecimal("25.50"),
                "Coffee",
                LocalDate.of(2024, 12, 20),
                testCategory
        );
        expense = expenseRepository.save(expense);

        mockMvc.perform(get("/api/expenses/" + expense.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount", is(25.50)))
                .andExpect(jsonPath("$.description", is("Coffee")));
    }

    /**
     * Tests that 404 is returned when expense does not exist.
     */
    @Test
    void getByIdNotFound() throws Exception {
        System.out.println("--- Now testing 404 response for non-existent expense ---");

        mockMvc.perform(get("/api/expenses/999"))
                .andExpect(status().isNotFound());
    }

    /**
     * Tests successful expense creation with valid data.
     */
    @Test
    void createValid() throws Exception {
        System.out.println("--- Now testing expense creation with valid data ---");

        CreateExpenseRequest request = new CreateExpenseRequest(
                new BigDecimal("45.00"),
                "Lunch",
                "2024-12-20",
                testCategory.getId()
        );

        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount", is(45.00)))
                .andExpect(jsonPath("$.description", is("Lunch")))
                .andExpect(jsonPath("$.categoryId", is(testCategory.getId().intValue())));
    }

    /**
     * Tests that bad request is returned when category does not exist.
     */
    @Test
    void createInvalidCategory() throws Exception {
        System.out.println("--- Now testing bad request for invalid category ---");

        CreateExpenseRequest request = new CreateExpenseRequest(
                new BigDecimal("45.00"),
                "Lunch",
                "2024-12-20",
                999L
        );

        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Tests successful expense update.
     */
    @Test
    void updateExists() throws Exception {
        System.out.println("--- Now testing expense update ---");

        Expense expense = new Expense(
                new BigDecimal("30.00"),
                "Old description",
                LocalDate.of(2024, 12, 15),
                testCategory
        );
        expense = expenseRepository.save(expense);

        CreateExpenseRequest request = new CreateExpenseRequest(
                new BigDecimal("50.00"),
                "Updated description",
                "2024-12-20",
                testCategory.getId()
        );

        mockMvc.perform(put("/api/expenses/" + expense.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount", is(50.00)))
                .andExpect(jsonPath("$.description", is("Updated description")));
    }

    /**
     * Tests that 404 is returned when updating non-existent expense.
     */
    @Test
    void updateNotFound() throws Exception {
        System.out.println("--- Now testing 404 response when updating non-existent expense ---");

        CreateExpenseRequest request = new CreateExpenseRequest(
                new BigDecimal("50.00"),
                "Description",
                "2024-12-20",
                testCategory.getId()
        );

        mockMvc.perform(put("/api/expenses/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    /**
     * Tests successful expense deletion.
     */
    @Test
    void deleteExists() throws Exception {
        System.out.println("--- Now testing expense deletion ---");

        Expense expense = new Expense(
                new BigDecimal("20.00"),
                "To delete",
                LocalDate.of(2024, 12, 15),
                testCategory
        );
        expense = expenseRepository.save(expense);

        mockMvc.perform(delete("/api/expenses/" + expense.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/expenses/" + expense.getId()))
                .andExpect(status().isNotFound());
    }

    /**
     * Tests that 404 is returned when deleting non-existent expense.
     */
    @Test
    void deleteNotFound() throws Exception {
        System.out.println("--- Now testing 404 response when deleting non-existent expense ---");

        mockMvc.perform(delete("/api/expenses/999"))
                .andExpect(status().isNotFound());
    }
}
