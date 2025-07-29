package com.raxrot.back.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterRequestDTO {
    private String type;           // "income" или "expense"
    private LocalDate startDate;
    private LocalDate endDate;
    private String keyword;        // например, "Salary"
    private String sortField;      // например, "date" или "amount"
    private String sortOrder;      // "asc" или "desc"
}
