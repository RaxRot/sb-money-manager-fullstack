package com.raxrot.back.services.impl;

import com.raxrot.back.dtos.IncomeRequestDTO;
import com.raxrot.back.dtos.IncomeResponseDTO;
import com.raxrot.back.dtos.UserResponseDTO;
import com.raxrot.back.entities.Category;
import com.raxrot.back.entities.Income;
import com.raxrot.back.entities.User;
import com.raxrot.back.exceptions.ApiException;
import com.raxrot.back.repositories.CategoryRepository;
import com.raxrot.back.repositories.IncomeRepository;
import com.raxrot.back.repositories.UserRepository;
import com.raxrot.back.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

class IncomeServiceImplTest {

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private IncomeRepository incomeRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private IncomeServiceImpl incomeService;

    private User user;
    private UserResponseDTO userDTO;
    private Category category;
    private IncomeRequestDTO incomeRequestDTO;
    private Income income;
    private IncomeResponseDTO incomeResponseDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = User.builder().id(1L).email("test@example.com").build();
        userDTO = UserResponseDTO.builder().email("test@example.com").build();

        category = new Category();
        category.setId(10L);

        incomeRequestDTO = IncomeRequestDTO.builder()
                .name("Salary")
                .amount(new BigDecimal("5000"))
                .categoryId(10L)
                .build();

        income = new Income();
        income.setId(1L);
        income.setUser(user);
        income.setCategory(category);
        income.setAmount(incomeRequestDTO.getAmount());

        incomeResponseDTO = IncomeResponseDTO.builder()
                .id(1L)
                .name("Salary")
                .amount(new BigDecimal("5000"))
                .build();
    }

    @Test
    void testCreateIncome_Success() {
        when(userService.getUserCurrentUser(null)).thenReturn(userDTO);
        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.of(user));
        when(categoryRepository.findById(10L)).thenReturn(Optional.of(category));
        when(modelMapper.map(incomeRequestDTO, Income.class)).thenReturn(income);
        when(incomeRepository.save(any(Income.class))).thenReturn(income);
        when(modelMapper.map(income, IncomeResponseDTO.class)).thenReturn(incomeResponseDTO);

        IncomeResponseDTO result = incomeService.create(incomeRequestDTO);

        assertEquals(incomeResponseDTO, result);
        verify(incomeRepository, times(1)).save(any(Income.class));
    }

    @Test
    void testGetCurrentMonthIncomesForCurrentUser() {
        when(userService.getUserCurrentUser(null)).thenReturn(userDTO);
        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.of(user));

        List<Income> incomes = List.of(income);
        when(incomeRepository.findByUserAndDateBetween(eq(user), any(), any())).thenReturn(incomes);
        when(modelMapper.map(income, IncomeResponseDTO.class)).thenReturn(incomeResponseDTO);

        List<IncomeResponseDTO> result = incomeService.getCurrentMonthIncomesForCurrentUser();

        assertEquals(1, result.size());
        assertEquals("Salary", result.get(0).getName());
    }

    @Test
    void testGetTotalIncomeForCurrentUser() {
        when(userService.getUserCurrentUser(null)).thenReturn(userDTO);
        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.of(user));
        when(incomeRepository.getTotalAmountByUser(user)).thenReturn(new BigDecimal("5000"));

        BigDecimal total = incomeService.getTotalIncomeForCurrentUser();

        assertEquals(new BigDecimal("5000"), total);
    }

    @Test
    void testDeleteIncome_Success() {
        income.setUser(user);

        when(userService.getUserCurrentUser(null)).thenReturn(userDTO);
        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.of(user));
        when(incomeRepository.findById(1L)).thenReturn(Optional.of(income));

        incomeService.deleteIncome(1L);

        verify(incomeRepository).delete(income);
    }

    @Test
    void testDeleteIncome_Unauthorized() {
        User anotherUser = User.builder().id(99L).build();
        income.setUser(anotherUser);

        when(userService.getUserCurrentUser(null)).thenReturn(userDTO);
        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.of(user));
        when(incomeRepository.findById(1L)).thenReturn(Optional.of(income));

        ApiException exception = assertThrows(ApiException.class, () -> {
            incomeService.deleteIncome(1L);
        });

        assertEquals("You do not have permission to delete this income", exception.getMessage());
    }

    @Test
    void testFilterIncomes() {
        Sort sort = Sort.by("date").descending();

        when(userService.getUserCurrentUser(null)).thenReturn(userDTO);
        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.of(user));

        List<Income> incomeList = List.of(income);
        when(incomeRepository.findByUserAndDateBetweenAndNameContainingIgnoreCase(
                eq(user), any(), any(), eq("Sal"), eq(sort))
        ).thenReturn(incomeList);

        when(modelMapper.map(income, IncomeResponseDTO.class)).thenReturn(incomeResponseDTO);

        List<IncomeResponseDTO> result = incomeService.filterIncomes(
                LocalDate.now().minusDays(10),
                LocalDate.now(),
                "Sal",
                sort
        );

        assertEquals(1, result.size());
        assertEquals("Salary", result.get(0).getName());
    }
}