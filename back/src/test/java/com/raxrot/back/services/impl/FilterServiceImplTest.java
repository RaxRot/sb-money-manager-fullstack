package com.raxrot.back.services.impl;

import com.raxrot.back.dtos.FilterRequestDTO;
import com.raxrot.back.exceptions.ApiException;
import com.raxrot.back.services.ExpenseService;
import com.raxrot.back.services.IncomeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class FilterServiceImplTest {
    private IncomeService incomeService;
    private ExpenseService expenseService;
    private FilterServiceImpl filterService;

    @BeforeEach
    void setUp() {
        incomeService = mock(IncomeService.class);
        expenseService = mock(ExpenseService.class);
        filterService = new FilterServiceImpl(incomeService, expenseService);
    }

    @Test
    void testFilterTransactions_income() {
        FilterRequestDTO filter = FilterRequestDTO.builder()
                .type("income")
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 12, 31))
                .keyword("salary")
                .sortField("amount")
                .sortOrder("desc")
                .build();

        when(incomeService.filterIncomes(
                any(LocalDate.class),
                any(LocalDate.class),
                anyString(),
                any(Sort.class))
        ).thenReturn(List.of());

        Object result = filterService.filterTransactions(filter);
        assertNotNull(result);
        assertTrue(result instanceof List);

        verify(incomeService, times(1)).filterIncomes(
                eq(LocalDate.of(2024, 1, 1)),
                eq(LocalDate.of(2024, 12, 31)),
                eq("salary"),
                any(Sort.class)
        );
        verifyNoInteractions(expenseService);
    }

    @Test
    void testFilterTransactions_expense() {
        FilterRequestDTO filter = FilterRequestDTO.builder()
                .type("expense")
                .build();

        when(expenseService.filterExpenses(
                any(LocalDate.class),
                any(LocalDate.class),
                anyString(),
                any(Sort.class))
        ).thenReturn(List.of());

        Object result = filterService.filterTransactions(filter);
        assertNotNull(result);
        assertTrue(result instanceof List);

        verify(expenseService, times(1)).filterExpenses(
                any(LocalDate.class),
                any(LocalDate.class),
                anyString(),
                any(Sort.class)
        );
        verifyNoInteractions(incomeService);
    }

    @Test
    void testFilterTransactions_invalidType_throwsException() {
        FilterRequestDTO filter = FilterRequestDTO.builder()
                .type("invalid-type")
                .build();

        ApiException ex = assertThrows(ApiException.class, () ->
                filterService.filterTransactions(filter)
        );
        assertEquals("Invalid type. Must be 'income' or 'expense'", ex.getMessage());

        verifyNoInteractions(incomeService);
        verifyNoInteractions(expenseService);
    }
}