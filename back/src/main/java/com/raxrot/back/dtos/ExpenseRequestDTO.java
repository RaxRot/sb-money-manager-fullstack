package com.raxrot.back.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExpenseRequestDTO {
    private String name;
    private String icon;
    private Long categoryId;
    private BigDecimal amount;
}
