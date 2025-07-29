package com.raxrot.back.services.impl;

import com.raxrot.back.dtos.ExpenseRequestDTO;
import com.raxrot.back.dtos.ExpenseResponseDTO;
import com.raxrot.back.dtos.UserResponseDTO;
import com.raxrot.back.entities.Category;
import com.raxrot.back.entities.Expense;
import com.raxrot.back.entities.User;
import com.raxrot.back.exceptions.ApiException;
import com.raxrot.back.repositories.CategoryRepository;
import com.raxrot.back.repositories.ExpenseRepository;
import com.raxrot.back.repositories.UserRepository;
import com.raxrot.back.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import static org.mockito.Mockito.*;

class ExpenseServiceImplTest {
    private ExpenseRepository expenseRepository;
    private CategoryRepository categoryRepository;
    private UserRepository userRepository;
    private UserService userService;
    private ModelMapper modelMapper;
    private ExpenseServiceImpl expenseService;

    private final User user = User.builder().id(1L).email("test@example.com").build();

    @BeforeEach
    void setUp() {
        expenseRepository = mock(ExpenseRepository.class);
        categoryRepository = mock(CategoryRepository.class);
        userRepository = mock(UserRepository.class);
        userService = mock(UserService.class);
        modelMapper = new ModelMapper();
        expenseService = new ExpenseServiceImpl(modelMapper, userService, userRepository, expenseRepository, categoryRepository);

        when(userService.getUserCurrentUser(null)).thenReturn(UserResponseDTO.builder().email(user.getEmail()).build());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
    }

    @Test
    void testCreate_success() {
        Category category = Category.builder().id(1L).name("Food").build();
        ExpenseRequestDTO requestDTO = ExpenseRequestDTO.builder()
                .name("Lunch").amount(BigDecimal.valueOf(10)).categoryId(1L).build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(expenseRepository.save(any(Expense.class))).thenAnswer(inv -> {
            Expense e = inv.getArgument(0);
            e.setId(99L);
            return e;
        });

        ExpenseResponseDTO response = expenseService.create(requestDTO);

        assertEquals("Lunch", response.getName());
        assertEquals(BigDecimal.valueOf(10), response.getAmount());
    }

    @Test
    void testCreate_categoryNotFound() {
        ExpenseRequestDTO requestDTO = ExpenseRequestDTO.builder().categoryId(42L).build();
        when(categoryRepository.findById(42L)).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class, () -> expenseService.create(requestDTO));
        assertEquals("Category not found", ex.getMessage());
    }

    @Test
    void testGetCurrentMonthExpensesForCurrentUser() {
        LocalDate now = LocalDate.now();
        Expense e = Expense.builder().name("Food").amount(BigDecimal.TEN).build();

        when(expenseRepository.findByUserAndDateBetween(eq(user), any(), any()))
                .thenReturn(List.of(e));

        List<ExpenseResponseDTO> list = expenseService.getCurrentMonthExpensesForCurrentUser();

        assertEquals(1, list.size());
        assertEquals("Food", list.get(0).getName());
    }

    @Test
    void testGetLatest5ExpensesForCurrentUser() {
        Expense e1 = Expense.builder().name("Coffee").amount(BigDecimal.ONE).build();
        when(expenseRepository.findTop5ByUserOrderByDateDesc(user)).thenReturn(List.of(e1));

        List<ExpenseResponseDTO> list = expenseService.getLatest5ExpensesForCurrentUser();

        assertEquals(1, list.size());
        assertEquals("Coffee", list.get(0).getName());
    }

    @Test
    void testGetTotalExpenseForCurrentUser() {
        when(expenseRepository.getTotalAmountByUser(user)).thenReturn(BigDecimal.valueOf(123.45));
        BigDecimal total = expenseService.getTotalExpenseForCurrentUser();
        assertEquals(BigDecimal.valueOf(123.45), total);
    }

    @Test
    void testFilterExpenses() {
        Expense e = Expense.builder().name("Gym").amount(BigDecimal.valueOf(30)).build();
        when(expenseRepository.findByUserAndDateBetweenAndNameContainingIgnoreCase(
                eq(user), any(), any(), eq("gym"), any(Sort.class)))
                .thenReturn(List.of(e));

        List<ExpenseResponseDTO> list = expenseService.filterExpenses(LocalDate.now().minusDays(1), LocalDate.now(), "gym", Sort.by("date"));
        assertEquals(1, list.size());
        assertEquals("Gym", list.get(0).getName());
    }

    @Test
    void testGetExpensesForUserOnDate() {
        Expense e = Expense.builder().name("Taxi").amount(BigDecimal.valueOf(15)).build();
        when(expenseRepository.findByUserAndDateBetween(eq(user), any(), any()))
                .thenReturn(List.of(e));

        List<ExpenseResponseDTO> list = expenseService.getExpensesForUserOnDate(LocalDate.now());
        assertEquals(1, list.size());
        assertEquals("Taxi", list.get(0).getName());
    }

    @Test
    void testDeleteExpense_success() {
        Expense e = Expense.builder().id(10L).user(user).build();
        when(expenseRepository.findById(10L)).thenReturn(Optional.of(e));

        expenseService.deleteExpense(10L);

        verify(expenseRepository, times(1)).delete(e);
    }

    @Test
    void testDeleteExpense_notOwner() {
        User another = User.builder().id(999L).build();
        Expense e = Expense.builder().id(1L).user(another).build();

        when(expenseRepository.findById(1L)).thenReturn(Optional.of(e));

        ApiException ex = assertThrows(ApiException.class, () -> expenseService.deleteExpense(1L));
        assertEquals("You do not have permission to delete this expense", ex.getMessage());
    }

    @Test
    void testDeleteExpense_notFound() {
        when(expenseRepository.findById(404L)).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class, () -> expenseService.deleteExpense(404L));
        assertEquals("Expense not found", ex.getMessage());
    }
}