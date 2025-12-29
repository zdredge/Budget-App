package com.budget.repository;

import com.budget.model.Category;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for CategoryRepository JPA operations.
 */
@DataJpaTest
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeAll
    static void beforeAll() {
        System.out.println("=== Running CategoryRepositoryTest ===");
    }

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAll();
    }

    /**
     * Tests that a category is persisted correctly.
     */
    @Test
    void save() {
        System.out.println("--- Now testing category save ---");

        Category category = new Category("Groceries", "#22c55e", new BigDecimal("500.00"), "Food items");

        Category saved = categoryRepository.save(category);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Groceries");
        assertThat(saved.getColor()).isEqualTo("#22c55e");
        assertThat(saved.getMonthlyLimit()).isEqualByComparingTo(new BigDecimal("500.00"));
    }

    /**
     * Tests that findByName returns the category when it exists.
     */
    @Test
    void findByNameExists() {
        System.out.println("--- Now testing findByName when category exists ---");

        Category category = new Category("Rent", "#3b82f6", BigDecimal.ZERO, "");
        categoryRepository.save(category);

        Optional<Category> found = categoryRepository.findByName("Rent");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Rent");
    }

    /**
     * Tests that findByName returns empty when category does not exist.
     */
    @Test
    void findByNameNotExists() {
        System.out.println("--- Now testing findByName when category does not exist ---");

        Optional<Category> found = categoryRepository.findByName("NonExistent");

        assertThat(found).isEmpty();
    }

    /**
     * Tests that existsByName returns true when category exists.
     */
    @Test
    void existsByNameTrue() {
        System.out.println("--- Now testing existsByName returns true ---");

        Category category = new Category("Utilities", "#f59e0b", BigDecimal.ZERO, "");
        categoryRepository.save(category);

        boolean exists = categoryRepository.existsByName("Utilities");

        assertThat(exists).isTrue();
    }

    /**
     * Tests that existsByName returns false when category does not exist.
     */
    @Test
    void existsByNameFalse() {
        System.out.println("--- Now testing existsByName returns false ---");

        boolean exists = categoryRepository.existsByName("NonExistent");

        assertThat(exists).isFalse();
    }

    /**
     * Tests that findAll returns all categories.
     */
    @Test
    void findAll() {
        System.out.println("--- Now testing findAll returns all categories ---");

        categoryRepository.save(new Category("Groceries", "#22c55e", BigDecimal.ZERO, ""));
        categoryRepository.save(new Category("Rent", "#3b82f6", BigDecimal.ZERO, ""));
        categoryRepository.save(new Category("Utilities", "#f59e0b", BigDecimal.ZERO, ""));

        var categories = categoryRepository.findAll();

        assertThat(categories).hasSize(3);
    }

    /**
     * Tests that delete removes a category.
     */
    @Test
    void delete() {
        System.out.println("--- Now testing category deletion ---");

        Category category = new Category("ToDelete", "#000000", BigDecimal.ZERO, "");
        category = categoryRepository.save(category);

        categoryRepository.delete(category);

        assertThat(categoryRepository.findById(category.getId())).isEmpty();
    }

    /**
     * Tests that a category can be updated.
     */
    @Test
    void update() {
        System.out.println("--- Now testing category update ---");

        Category category = new Category("Old Name", "#000000", BigDecimal.ZERO, "");
        category = categoryRepository.save(category);

        category.setName("New Name");
        category.setMonthlyLimit(new BigDecimal("1000.00"));
        categoryRepository.save(category);

        Category updated = categoryRepository.findById(category.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("New Name");
        assertThat(updated.getMonthlyLimit()).isEqualByComparingTo(new BigDecimal("1000.00"));
    }
}
