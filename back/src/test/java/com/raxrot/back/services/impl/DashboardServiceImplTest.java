package com.raxrot.back.services.impl;

import com.raxrot.back.dtos.*;
import com.raxrot.back.services.ExpenseService;
import com.raxrot.back.services.IncomeService;
import com.raxrot.back.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTest {

    @Mock
    private IncomeService incomeService;

    @Mock
    private ExpenseService expenseService;

    @Mock
    private UserService userService;

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    @Test
    void testGetDashboardData() {
        // Подготовка данных
        Long userId = 1L;
        UserResponseDTO user = UserResponseDTO.builder()
                .id(userId)
                .fullName("John Doe")
                .email("john@example.com")
                .build();

        List<IncomeResponseDTO> incomes = List.of(
                createIncome(101L, "Salary", new BigDecimal("1000"), LocalDate.now().minusDays(1))
        );
        List<ExpenseResponseDTO> expenses = List.of(
                createExpense(201L, "Groceries", new BigDecimal("200"), LocalDate.now())
        );

        BigDecimal totalIncome = new BigDecimal("1000");
        BigDecimal totalExpense = new BigDecimal("200");

        // Мокаем зависимости
        when(userService.getUserCurrentUser(null)).thenReturn(user);
        when(incomeService.getLatest5IncomesForCurrentUser()).thenReturn(incomes);
        when(expenseService.getLatest5ExpensesForCurrentUser()).thenReturn(expenses);
        when(incomeService.getTotalIncomeForCurrentUser()).thenReturn(totalIncome);
        when(expenseService.getTotalExpenseForCurrentUser()).thenReturn(totalExpense);

        // Выполнение
        DashboardResponseDTO result = dashboardService.getDashboardData();

        // Проверки
        assertNotNull(result);
        assertEquals(totalIncome, result.getTotalIncome());
        assertEquals(totalExpense, result.getTotalExpense());
        assertEquals(new BigDecimal("800"), result.getTotalBalance());

        assertEquals(1, result.getRecent5Incomes().size());
        assertEquals(1, result.getRecent5Expenses().size());
        assertEquals(2, result.getRecentTransactions().size());

        RecentTransactionDTO txIncome = result.getRecentTransactions().stream()
                .filter(tx -> tx.getType().equals("income"))
                .findFirst().orElseThrow();

        assertEquals("Salary", txIncome.getName());
        assertEquals(userId, txIncome.getProfileId());
        assertEquals(new BigDecimal("1000"), txIncome.getAmount());

        RecentTransactionDTO txExpense = result.getRecentTransactions().stream()
                .filter(tx -> tx.getType().equals("expense"))
                .findFirst().orElseThrow();

        assertEquals("Groceries", txExpense.getName());
        assertEquals(new BigDecimal("200"), txExpense.getAmount());
    }

    private IncomeResponseDTO createIncome(Long id, String name, BigDecimal amount, LocalDate date) {
        return IncomeResponseDTO.builder()
                .id(id)
                .name(name)
                .icon("💰")
                .amount(amount)
                .date(date)
                .createdAt(LocalDateTime.now().minusDays(2))
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private ExpenseResponseDTO createExpense(Long id, String name, BigDecimal amount, LocalDate date) {
        return ExpenseResponseDTO.builder()
                .id(id)
                .name(name)
                .icon("🛒")
                .amount(amount)
                .date(date)
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
