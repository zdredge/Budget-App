package com.budget.repository;

import com.budget.model.Category;
import com.budget.model.Expense;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for ExpenseRepository JPA operations.
 */
@DataJpaTest
class ExpenseRepositoryTest {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category groceries;
    private Category rent;

    @BeforeAll
    static void beforeAll() {
        System.out.println("=== Running ExpenseRepositoryTest ===");
    }

    @BeforeEach
    void setUp() {
        expenseRepository.deleteAll();
        categoryRepository.deleteAll();

        groceries = new Category("Groceries", "#22c55e", BigDecimal.ZERO, "");
        groceries = categoryRepository.save(groceries);

        rent = new Category("Rent", "#3b82f6", BigDecimal.ZERO, "");
        rent = categoryRepository.save(rent);
    }

    /**
     * Tests that an expense is persisted correctly.
     */
    @Test
    void save() {
        System.out.println("--- Now testing expense save ---");

        Expense expense = new Expense(
                new BigDecimal("50.00"),
                "Weekly groceries",
                LocalDate.of(2024, 12, 15),
                groceries
        );

        Expense saved = expenseRepository.save(expense);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getAmount()).isEqualByComparingTo(new BigDecimal("50.00"));
        assertThat(saved.getDescription()).isEqualTo("Weekly groceries");
        assertThat(saved.getDate()).isEqualTo(LocalDate.of(2024, 12, 15));
        assertThat(saved.getCategory().getName()).isEqualTo("Groceries");
    }

    /**
     * Tests that findByMonth returns expenses for the specified month.
     */
    @Test
    void findByMonth() {
        System.out.println("--- Now testing findByMonth returns correct expenses ---");

        expenseRepository.save(new Expense(
                new BigDecimal("100.00"),
                "Dec expense 1",
                LocalDate.of(2024, 12, 10),
                groceries
        ));
        expenseRepository.save(new Expense(
                new BigDecimal("200.00"),
                "Dec expense 2",
                LocalDate.of(2024, 12, 20),
                rent
        ));
        expenseRepository.save(new Expense(
                new BigDecimal("150.00"),
                "Nov expense",
                LocalDate.of(2024, 11, 15),
                groceries
        ));

        List<Expense> decemberExpenses = expenseRepository.findByMonth(2024, 12);

        assertThat(decemberExpenses).hasSize(2);
        assertThat(decemberExpenses).allMatch(e -> e.getDate().getMonthValue() == 12);
    }

    /**
     * Tests that findByMonth returns empty when no expenses exist for the month.
     */
    @Test
    void findByMonthEmpty() {
        System.out.println("--- Now testing findByMonth returns empty for no expenses ---");

        expenseRepository.save(new Expense(
                new BigDecimal("100.00"),
                "December expense",
                LocalDate.of(2024, 12, 15),
                groceries
        ));

        List<Expense> novemberExpenses = expenseRepository.findByMonth(2024, 11);

        assertThat(novemberExpenses).isEmpty();
    }

    /**
     * Tests that findByMonth orders expenses by date descending.
     */
    @Test
    void findByMonthOrderByDateDesc() {
        System.out.println("--- Now testing findByMonth orders by date descending ---");

        expenseRepository.save(new Expense(
                new BigDecimal("50.00"),
                "Early month",
                LocalDate.of(2024, 12, 5),
                groceries
        ));
        expenseRepository.save(new Expense(
                new BigDecimal("75.00"),
                "Late month",
                LocalDate.of(2024, 12, 25),
                groceries
        ));
        expenseRepository.save(new Expense(
                new BigDecimal("100.00"),
                "Mid month",
                LocalDate.of(2024, 12, 15),
                groceries
        ));

        List<Expense> expenses = expenseRepository.findByMonth(2024, 12);

        assertThat(expenses).hasSize(3);
        assertThat(expenses.get(0).getDate()).isEqualTo(LocalDate.of(2024, 12, 25));
        assertThat(expenses.get(1).getDate()).isEqualTo(LocalDate.of(2024, 12, 15));
        assertThat(expenses.get(2).getDate()).isEqualTo(LocalDate.of(2024, 12, 5));
    }

    /**
     * Tests that findByCategoryAndMonth returns filtered expenses.
     */
    @Test
    void findByCategoryAndMonth() {
        System.out.println("--- Now testing findByCategoryAndMonth returns filtered expenses ---");

        expenseRepository.save(new Expense(
                new BigDecimal("100.00"),
                "Groceries Dec",
                LocalDate.of(2024, 12, 15),
                groceries
        ));
        expenseRepository.save(new Expense(
                new BigDecimal("2000.00"),
                "Rent Dec",
                LocalDate.of(2024, 12, 1),
                rent
        ));

        List<Expense> groceriesExpenses = expenseRepository.findByCategoryAndMonth(
                groceries.getId(), 2024, 12
        );

        assertThat(groceriesExpenses).hasSize(1);
        assertThat(groceriesExpenses.get(0).getCategory().getName()).isEqualTo("Groceries");
    }

    /**
     * Tests that findByDateBetween returns expenses within the date range.
     */
    @Test
    void findByDateBetween() {
        System.out.println("--- Now testing findByDateBetween returns expenses in range ---");

        expenseRepository.save(new Expense(
                new BigDecimal("50.00"),
                "In range",
                LocalDate.of(2024, 12, 15),
                groceries
        ));
        expenseRepository.save(new Expense(
                new BigDecimal("100.00"),
                "Out of range",
                LocalDate.of(2024, 11, 15),
                groceries
        ));

        List<Expense> expenses = expenseRepository.findByDateBetweenOrderByDateDesc(
                LocalDate.of(2024, 12, 1),
                LocalDate.of(2024, 12, 31)
        );

        assertThat(expenses).hasSize(1);
        assertThat(expenses.get(0).getDescription()).isEqualTo("In range");
    }

    /**
     * Tests that delete removes an expense.
     */
    @Test
    void delete() {
        System.out.println("--- Now testing expense deletion ---");

        Expense expense = new Expense(
                new BigDecimal("50.00"),
                "To delete",
                LocalDate.of(2024, 12, 15),
                groceries
        );
        expense = expenseRepository.save(expense);

        expenseRepository.delete(expense);

        assertThat(expenseRepository.findById(expense.getId())).isEmpty();
    }

    /**
     * Tests that an expense can be updated.
     */
    @Test
    void update() {
        System.out.println("--- Now testing expense update ---");

        Expense expense = new Expense(
                new BigDecimal("50.00"),
                "Original",
                LocalDate.of(2024, 12, 15),
                groceries
        );
        expense = expenseRepository.save(expense);

        expense.setAmount(new BigDecimal("100.00"));
        expense.setDescription("Updated");
        expenseRepository.save(expense);

        Expense updated = expenseRepository.findById(expense.getId()).orElseThrow();
        assertThat(updated.getAmount()).isEqualByComparingTo(new BigDecimal("100.00"));
        assertThat(updated.getDescription()).isEqualTo("Updated");
    }
}
