package com.raxrot.back.repositories;

import com.raxrot.back.entities.Expense;
import com.raxrot.back.entities.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense>findByUser(User user);
    List<Expense>findTop5ByUserOrderByDateDesc(User user);
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.user = :user")
    BigDecimal getTotalAmountByUser(@Param("user") User user);

    List<Expense> findByUserAndDateBetweenAndNameContainingIgnoreCase(
            User user,
            LocalDate startDate,
            LocalDate endDate,
            String keyword,
            Sort sort
    );

    List<Expense> findByUserAndDateBetween(
            User user,
            LocalDate startDate,
            LocalDate endDate
    );

    List<Expense>getExpensesForUserOnDate(User user,LocalDate date);
}
