package com.raxrot.back.services.impl;

import com.raxrot.back.dtos.FilterRequestDTO;
import com.raxrot.back.exceptions.ApiException;
import com.raxrot.back.services.ExpenseService;
import com.raxrot.back.services.FilterService;
import com.raxrot.back.services.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class FilterServiceImpl implements FilterService {
    private final IncomeService incomeService;
    private final ExpenseService expenseService;

    @Override
    public Object filterTransactions(FilterRequestDTO filter) {
        LocalDate startDate = filter.getStartDate() != null ? filter.getStartDate() : LocalDate.MIN;
        LocalDate endDate = filter.getEndDate() != null ? filter.getEndDate() : LocalDate.now();
        String keyword = filter.getKeyword() != null ? filter.getKeyword() : "";
        String sortField = filter.getSortField() != null ? filter.getSortField() : "date";
        Sort.Direction direction = "desc".equalsIgnoreCase(filter.getSortOrder()) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortField);

        return switch (filter.getType().toLowerCase()) {
            case "income" -> incomeService.filterIncomes(startDate, endDate, keyword, sort);
            case "expense" -> expenseService.filterExpenses(startDate, endDate, keyword, sort);
            default -> throw new ApiException("Invalid type. Must be 'income' or 'expense'");
        };
    }
}
