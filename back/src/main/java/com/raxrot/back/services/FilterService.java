package com.raxrot.back.services;

import com.raxrot.back.dtos.FilterRequestDTO;

public interface FilterService {
    Object filterTransactions(FilterRequestDTO filter);
}
