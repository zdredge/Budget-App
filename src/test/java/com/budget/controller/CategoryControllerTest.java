package com.budget.controller;

import com.budget.dto.CategoryDTO;
import com.budget.model.Category;
import com.budget.repository.CategoryRepository;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for CategoryController REST endpoints.
 */
@SpringBootTest
@AutoConfigureMockMvc
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll() {
        System.out.println("=== Running CategoryControllerTest ===");
    }

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAll();
    }

    /**
     * Tests that an empty list is returned when no categories exist.
     */
    @Test
    void getAllEmpty() throws Exception {
        System.out.println("--- Now testing for no present categories ---");

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    /**
     * Tests that all categories are returned when they exist.
     */
    @Test
    void getAllWithData() throws Exception {
        System.out.println("--- Now testing retrieval of all categories ---");

        Category category = new Category("Groceries", "#22c55e", BigDecimal.ZERO, "");
        categoryRepository.save(category);

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Groceries")))
                .andExpect(jsonPath("$[0].color", is("#22c55e")));
    }

    /**
     * Tests that a single category is returned by ID.
     */
    @Test
    void getByIdExists() throws Exception {
        System.out.println("--- Now testing retrieval of category by ID ---");

        Category category = new Category("Rent", "#3b82f6", BigDecimal.ZERO, "");
        category = categoryRepository.save(category);

        mockMvc.perform(get("/api/categories/" + category.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Rent")))
                .andExpect(jsonPath("$.color", is("#3b82f6")));
    }

    /**
     * Tests that 404 is returned when category does not exist.
     */
    @Test
    void getByIdNotFound() throws Exception {
        System.out.println("--- Now testing 404 response for non-existent category ---");

        mockMvc.perform(get("/api/categories/999"))
                .andExpect(status().isNotFound());
    }

    /**
     * Tests successful category creation with valid data.
     */
    @Test
    void createValid() throws Exception {
        System.out.println("--- Now testing category creation with valid data ---");

        CategoryDTO request = new CategoryDTO(null, "Utilities", new BigDecimal("200.00"), "#f59e0b", "Monthly utilities");

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Utilities")))
                .andExpect(jsonPath("$.monthlyLimit", is(200.00)))
                .andExpect(jsonPath("$.color", is("#f59e0b")))
                .andExpect(jsonPath("$.description", is("Monthly utilities")));
    }

    /**
     * Tests that duplicate category names are rejected.
     */
    @Test
    void createDuplicateName() throws Exception {
        System.out.println("--- Now testing rejection of duplicate category name ---");

        Category existing = new Category("Groceries", "#22c55e", BigDecimal.ZERO, "");
        categoryRepository.save(existing);

        CategoryDTO request = new CategoryDTO(null, "Groceries", BigDecimal.ZERO, "#ff0000", "");

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Tests successful category update.
     */
    @Test
    void updateExists() throws Exception {
        System.out.println("--- Now testing category update ---");

        Category category = new Category("Old Name", "#000000", new BigDecimal("100.00"), "");
        category = categoryRepository.save(category);

        CategoryDTO request = new CategoryDTO(null, "New Name", new BigDecimal("500.00"), "#ffffff", "Updated description");

        mockMvc.perform(put("/api/categories/" + category.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("New Name")))
                .andExpect(jsonPath("$.monthlyLimit", is(500.00)))
                .andExpect(jsonPath("$.color", is("#ffffff")))
                .andExpect(jsonPath("$.description", is("Updated description")));
    }

    /**
     * Tests that 404 is returned when updating non-existent category.
     */
    @Test
    void updateNotFound() throws Exception {
        System.out.println("--- Now testing 404 response when updating non-existent category ---");

        CategoryDTO request = new CategoryDTO(null, "Name", BigDecimal.ZERO, "#000000", "");

        mockMvc.perform(put("/api/categories/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    /**
     * Tests successful category deletion.
     */
    @Test
    void deleteExists() throws Exception {
        System.out.println("--- Now testing category deletion ---");

        Category category = new Category("ToDelete", "#000000", BigDecimal.ZERO, "");
        category = categoryRepository.save(category);

        mockMvc.perform(delete("/api/categories/" + category.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/categories/" + category.getId()))
                .andExpect(status().isNotFound());
    }

    /**
     * Tests that 404 is returned when deleting non-existent category.
     */
    @Test
    void deleteNotFound() throws Exception {
        System.out.println("--- Now testing 404 response when deleting non-existent category ---");

        mockMvc.perform(delete("/api/categories/999"))
                .andExpect(status().isNotFound());
    }
}
