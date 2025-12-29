package com.budget.controller;

import com.budget.dto.CreateExpenseRequest;
import com.budget.dto.ExpenseDTO;
import com.budget.model.Category;
import com.budget.model.Expense;
import com.budget.repository.CategoryRepository;
import com.budget.repository.ExpenseRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

/**
 * REST controller for managing expenses.
 */
@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;

    public ExpenseController(ExpenseRepository expenseRepository, CategoryRepository categoryRepository) {
        this.expenseRepository = expenseRepository;
        this.categoryRepository = categoryRepository;
    }

    /**
     * Returns expenses, optionally filtered by month.
     * @param month optional month in "YYYY-MM" format
     */
    @GetMapping
    public List<ExpenseDTO> getExpenses(@RequestParam(required = false) String month) {
        if (month != null) {
            YearMonth yearMonth = YearMonth.parse(month);
            return expenseRepository.findByMonth(yearMonth.getYear(), yearMonth.getMonthValue())
                    .stream()
                    .map(this::toDTO)
                    .toList();
        }
        return expenseRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    /**
     * Returns an expense by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ExpenseDTO> getExpense(@PathVariable Long id) {
        return expenseRepository.findById(id)
                .map(this::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Creates a new expense. Returns bad request if category doesn't exist.
     */
    @PostMapping
    public ResponseEntity<ExpenseDTO> createExpense(@RequestBody CreateExpenseRequest request) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElse(null);

        if (category == null) {
            return ResponseEntity.badRequest().build();
        }

        Expense expense = new Expense();
        expense.setAmount(request.amount());
        expense.setDescription(request.description());
        expense.setDate(LocalDate.parse(request.date()));
        expense.setCategory(category);

        Expense saved = expenseRepository.save(expense);
        return ResponseEntity.ok(toDTO(saved));
    }

    /**
     * Updates an existing expense by ID.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ExpenseDTO> updateExpense(@PathVariable Long id, @RequestBody CreateExpenseRequest request) {
        return expenseRepository.findById(id)
                .map(expense -> {
                    if (request.amount() != null) {
                        expense.setAmount(request.amount());
                    }
                    if (request.description() != null) {
                        expense.setDescription(request.description());
                    }
                    if (request.date() != null) {
                        expense.setDate(LocalDate.parse(request.date()));
                    }
                    if (request.categoryId() != null) {
                        categoryRepository.findById(request.categoryId())
                                .ifPresent(expense::setCategory);
                    }
                    return ResponseEntity.ok(toDTO(expenseRepository.save(expense)));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Deletes an expense by ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        if (!expenseRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        expenseRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Converts an Expense entity to an ExpenseDTO.
     */
    private ExpenseDTO toDTO(Expense expense) {
        return new ExpenseDTO(
                expense.getId(),
                expense.getAmount(),
                expense.getDescription(),
                expense.getDate(),
                expense.getCategory().getId(),
                expense.getCategory().getName(),
                expense.getCategory().getColor()
        );
    }
}

