package com.raxrot.back.services;

import com.raxrot.back.dtos.IncomeRequestDTO;
import com.raxrot.back.dtos.IncomeResponseDTO;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface IncomeService {
    IncomeResponseDTO create(IncomeRequestDTO incomeRequestDTO);
    List<IncomeResponseDTO> getCurrentMonthIncomesForCurrentUser();
    List<IncomeResponseDTO> getLatest5IncomesForCurrentUser();
    BigDecimal getTotalIncomeForCurrentUser();
    List<IncomeResponseDTO> filterIncomes(LocalDate startDate, LocalDate endDate, String keyword, Sort sort);
    List<IncomeResponseDTO> getIncomesForUserOnDate(LocalDate date);
    void deleteIncome(Long incomeId);
}