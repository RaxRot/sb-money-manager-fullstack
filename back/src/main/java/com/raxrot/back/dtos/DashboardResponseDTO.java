package com.raxrot.back.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponseDTO {
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal totalBalance;

    private List<IncomeResponseDTO> recent5Incomes;
    private List<ExpenseResponseDTO> recent5Expenses;
    private List<RecentTransactionDTO> recentTransactions;
}