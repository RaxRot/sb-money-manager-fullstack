package com.raxrot.back.repositories;

import com.raxrot.back.entities.Income;
import com.raxrot.back.entities.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface IncomeRepository extends JpaRepository<Income, Long> {
    List<Income> findByUser(User user);
    List<Income> findTop5ByUserOrderByDateDesc(User user);

    @Query("SELECT COALESCE(SUM(i.amount), 0) FROM Income i WHERE i.user = :user")
    BigDecimal getTotalAmountByUser(@Param("user") User user);

    List<Income> findByUserAndDateBetweenAndNameContainingIgnoreCase(
            User user,
            LocalDate startDate,
            LocalDate endDate,
            String keyword,
            Sort sort
    );

    List<Income> findByUserAndDateBetween(
            User user,
            LocalDate startDate,
            LocalDate endDate
    );
}