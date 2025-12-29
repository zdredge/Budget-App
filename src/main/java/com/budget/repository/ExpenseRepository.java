package com.budget.repository;

import com.budget.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByDateBetweenOrderByDateDesc(LocalDate startDate, LocalDate endDate);

    @Query("SELECT e FROM Expense e WHERE YEAR(e.date) = :year AND MONTH(e.date) = :month ORDER BY e.date DESC")
    List<Expense> findByMonth(@Param("year") int year, @Param("month") int month);

    @Query("SELECT e FROM Expense e WHERE e.category.id = :categoryId AND YEAR(e.date) = :year AND MONTH(e.date) = :month")
    List<Expense> findByCategoryAndMonth(@Param("categoryId") Long categoryId, @Param("year") int year, @Param("month") int month);
}

