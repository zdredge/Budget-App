package com.budget.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private BigDecimal monthlyLimit;

    private String description;

    @Column(length = 7)
    private String color = "#6b7280"; // Default gray color

    public Category() {}

    public Category(String name, String color, BigDecimal monthlyLimit, String description) {
        this.name = name;
        this.color = color;
        this.monthlyLimit = monthlyLimit != null ? monthlyLimit : BigDecimal.ZERO;
        this.description = description != null ? description : "";
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getMonthlyLimit() {
        return monthlyLimit;
    }

    public void setMonthlyLimit(BigDecimal monthlyLimit) {
        this.monthlyLimit = monthlyLimit;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
