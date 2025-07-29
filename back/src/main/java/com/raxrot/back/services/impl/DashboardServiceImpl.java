package com.raxrot.back.services.impl;

import com.raxrot.back.dtos.*;
import com.raxrot.back.services.DashboardService;
import com.raxrot.back.services.ExpenseService;
import com.raxrot.back.services.IncomeService;
import com.raxrot.back.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final IncomeService incomeService;
    private final ExpenseService expenseService;
    private final UserService userService;

    @Override
    public DashboardResponseDTO getDashboardData() {
        UserResponseDTO user = userService.getUserCurrentUser(null);
        Long profileId = user.getId();

        // 5 последних доходов и расходов
        List<IncomeResponseDTO> latestIncomes = incomeService.getLatest5IncomesForCurrentUser();
        List<ExpenseResponseDTO> latestExpenses = expenseService.getLatest5ExpensesForCurrentUser();

        // Преобразуем в RecentTransactionDTO и объединяем
        List<RecentTransactionDTO> recentTransactions = Stream.concat(
                latestIncomes.stream().map(i -> toTransaction(i, "income", profileId)),
                latestExpenses.stream().map(e -> toTransaction(e, "expense", profileId))
        ).sorted(Comparator
                .comparing(RecentTransactionDTO::getDate, Comparator.nullsLast(Comparator.reverseOrder()))
                .thenComparing(RecentTransactionDTO::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder()))
        ).toList();

        BigDecimal totalIncome = incomeService.getTotalIncomeForCurrentUser();
        BigDecimal totalExpense = expenseService.getTotalExpenseForCurrentUser();

        return DashboardResponseDTO.builder()
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .totalBalance(totalIncome.subtract(totalExpense))
                .recent5Incomes(latestIncomes)
                .recent5Expenses(latestExpenses)
                .recentTransactions(recentTransactions)
                .build();
    }

    private RecentTransactionDTO toTransaction(IncomeResponseDTO dto, String type, Long profileId) {
        return RecentTransactionDTO.builder()
                .id(dto.getId())
                .profileId(profileId)
                .name(dto.getName())
                .icon(dto.getIcon())
                .amount(dto.getAmount())
                .date(dto.getDate())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .type(type)
                .build();
    }

    private RecentTransactionDTO toTransaction(ExpenseResponseDTO dto, String type, Long profileId) {
        return RecentTransactionDTO.builder()
                .id(dto.getId())
                .profileId(profileId)
                .name(dto.getName())
                .icon(dto.getIcon())
                .amount(dto.getAmount())
                .date(dto.getDate())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .type(type)
                .build();
    }
}
