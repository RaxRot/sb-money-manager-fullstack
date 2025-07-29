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
import com.raxrot.back.services.ExpenseService;
import com.raxrot.back.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
@Service
public class ExpenseServiceImpl implements ExpenseService {

    private final ModelMapper modelMapper;
    private final UserService userService;
    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;

    public ExpenseServiceImpl(ModelMapper modelMapper,
                              UserService userService,
                              UserRepository userRepository,
                              ExpenseRepository expenseRepository,
                              CategoryRepository categoryRepository) {
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.userRepository = userRepository;
        this.expenseRepository = expenseRepository;
        this.categoryRepository = categoryRepository;
    }

    private User getCurrentUser() {
        UserResponseDTO dto = userService.getUserCurrentUser(null);
        return userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new ApiException("User not found"));
    }

    @Override
    public ExpenseResponseDTO create(ExpenseRequestDTO expenseRequestDTO) {
        User user = getCurrentUser();

        Category category = categoryRepository.findById(expenseRequestDTO.getCategoryId())
                .orElseThrow(() -> new ApiException("Category not found"));

        Expense expense = modelMapper.map(expenseRequestDTO, Expense.class);
        expense.setUser(user);
        expense.setCategory(category);
        expense.setId(null);//WITHOUT IT NOT WORK!!!DONT TOUCH!!!

        Expense savedExpense = expenseRepository.save(expense);
        return modelMapper.map(savedExpense, ExpenseResponseDTO.class);
    }

    @Override
    public List<ExpenseResponseDTO> getCurrentMonthExpensesForCurrentUser() {
        User user = getCurrentUser();
        LocalDate now = LocalDate.now();
        LocalDate start = now.withDayOfMonth(1);
        LocalDate end = now.withDayOfMonth(now.lengthOfMonth());

        return expenseRepository.findByUserAndDateBetween(user, start, end).stream()
                .map(e -> modelMapper.map(e, ExpenseResponseDTO.class))
                .toList();
    }

    @Override
    public List<ExpenseResponseDTO> getLatest5ExpensesForCurrentUser() {
        User user = getCurrentUser();
        return expenseRepository.findTop5ByUserOrderByDateDesc(user).stream()
                .map(e -> modelMapper.map(e, ExpenseResponseDTO.class))
                .toList();
    }

    @Override
    public BigDecimal getTotalExpenseForCurrentUser() {
        User user = getCurrentUser();
        return expenseRepository.getTotalAmountByUser(user);
    }

    @Override
    public List<ExpenseResponseDTO> filterExpenses(LocalDate startDate, LocalDate endDate, String keyword, Sort sort) {
        User user = getCurrentUser();
        return expenseRepository
                .findByUserAndDateBetweenAndNameContainingIgnoreCase(user, startDate, endDate, keyword, sort)
                .stream()
                .map(e -> modelMapper.map(e, ExpenseResponseDTO.class))
                .toList();
    }

    @Override
    public List<ExpenseResponseDTO> getExpensesForUserOnDate(LocalDate date) {
        User user = getCurrentUser();
        return expenseRepository.findByUserAndDateBetween(user, date, date).stream()
                .map(e -> modelMapper.map(e, ExpenseResponseDTO.class))
                .toList();
    }

    @Override
    public void deleteExpense(Long expenseId) {
        User user = getCurrentUser();
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ApiException("Expense not found"));

        if (!expense.getUser().getId().equals(user.getId())) {
            throw new ApiException("You do not have permission to delete this expense");
        }

        expenseRepository.delete(expense);
    }
}
