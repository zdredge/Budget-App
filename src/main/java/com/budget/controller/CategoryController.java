package com.budget.controller;

import com.budget.dto.CategoryDTO;
import com.budget.model.Category;
import com.budget.repository.CategoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing budget categories.
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryRepository categoryRepository;

    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Returns all categories.
     */
    @GetMapping
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    /**
     * Returns a category by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategory(@PathVariable Long id) {
        return categoryRepository.findById(id)
                .map(this::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Creates a new category. Returns bad request if name already exists.
     */
    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody CategoryDTO request) {
        if (categoryRepository.existsByName(request.name())) {
            return ResponseEntity.badRequest().build();
        }

        Category category = new Category();
        category.setName(request.name());
        category.setMonthlyLimit(request.monthlyLimit() != null ? request.monthlyLimit() : java.math.BigDecimal.ZERO);
        category.setColor(request.color() != null ? request.color() : "#6b7280");
        category.setDescription(request.description() != null ? request.description() : "");

        Category saved = categoryRepository.save(category);
        return ResponseEntity.ok(toDTO(saved));
    }

    /**
     * Updates an existing category by ID.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long id, @RequestBody CategoryDTO request) {
        return categoryRepository.findById(id)
                .map(category -> {
                    if (request.name() != null) {
                        category.setName(request.name());
                    }
                    if (request.monthlyLimit() != null) {
                        category.setMonthlyLimit(request.monthlyLimit());
                    }
                    if (request.color() != null) {
                        category.setColor(request.color());
                    }
                    if (request.description() != null) {
                        category.setDescription(request.description());
                    }
                    return ResponseEntity.ok(toDTO(categoryRepository.save(category)));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Deletes a category by ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        if (!categoryRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        categoryRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Converts a Category entity to a CategoryDTO.
     */
    private CategoryDTO toDTO(Category category) {
        return new CategoryDTO(
                category.getId(),
                category.getName(),
                category.getMonthlyLimit(),
                category.getColor(),
                category.getDescription()
        );
    }
}
