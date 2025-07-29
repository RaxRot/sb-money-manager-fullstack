package com.raxrot.back.services;

import com.raxrot.back.dtos.ExpenseRequestDTO;
import com.raxrot.back.dtos.ExpenseResponseDTO;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseService {
    ExpenseResponseDTO create(ExpenseRequestDTO expenseRequestDTO);
    List<ExpenseResponseDTO> getCurrentMonthExpensesForCurrentUser();
    List<ExpenseResponseDTO> getLatest5ExpensesForCurrentUser();
    BigDecimal getTotalExpenseForCurrentUser();
    List<ExpenseResponseDTO> filterExpenses(LocalDate startDate, LocalDate endDate, String keyword, Sort sort);
    List<ExpenseResponseDTO> getExpensesForUserOnDate(LocalDate date);
    void deleteExpense(Long expenseId);
}
