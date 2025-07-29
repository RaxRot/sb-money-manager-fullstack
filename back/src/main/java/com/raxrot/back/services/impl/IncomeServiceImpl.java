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
import com.raxrot.back.services.IncomeService;
import com.raxrot.back.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class IncomeServiceImpl implements IncomeService {

    private final ModelMapper modelMapper;
    private final UserService userService;
    private final UserRepository userRepository;
    private final IncomeRepository incomeRepository;
    private final CategoryRepository categoryRepository;

    public IncomeServiceImpl(ModelMapper modelMapper,
                             UserService userService,
                             UserRepository userRepository,
                             IncomeRepository incomeRepository,
                             CategoryRepository categoryRepository) {
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.userRepository = userRepository;
        this.incomeRepository = incomeRepository;
        this.categoryRepository = categoryRepository;
    }

    private User getCurrentUser() {
        UserResponseDTO dto = userService.getUserCurrentUser(null);
        return userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new ApiException("User not found"));
    }

    @Override
    public IncomeResponseDTO create(IncomeRequestDTO incomeRequestDTO) {
        User user = getCurrentUser();

        Category category = categoryRepository.findById(incomeRequestDTO.getCategoryId())
                .orElseThrow(() -> new ApiException("Category not found"));

        Income income = modelMapper.map(incomeRequestDTO, Income.class);
        income.setUser(user);
        income.setCategory(category);
        income.setId(null);//WITHOUT IT NOT WORK!!!DONT TOUCH!!!

        Income savedIncome = incomeRepository.save(income);
        return modelMapper.map(savedIncome, IncomeResponseDTO.class);
    }

    @Override
    public List<IncomeResponseDTO> getCurrentMonthIncomesForCurrentUser() {
        User user = getCurrentUser();
        LocalDate now = LocalDate.now();
        LocalDate start = now.withDayOfMonth(1);
        LocalDate end = now.withDayOfMonth(now.lengthOfMonth());

        return incomeRepository.findByUserAndDateBetween(user, start, end).stream()
                .map(i -> modelMapper.map(i, IncomeResponseDTO.class))
                .toList();
    }

    @Override
    public List<IncomeResponseDTO> getLatest5IncomesForCurrentUser() {
        User user = getCurrentUser();
        return incomeRepository.findTop5ByUserOrderByDateDesc(user).stream()
                .map(i -> modelMapper.map(i, IncomeResponseDTO.class))
                .toList();
    }

    @Override
    public BigDecimal getTotalIncomeForCurrentUser() {
        User user = getCurrentUser();
        return incomeRepository.getTotalAmountByUser(user);
    }

    @Override
    public List<IncomeResponseDTO> filterIncomes(LocalDate startDate, LocalDate endDate, String keyword, Sort sort) {
        User user = getCurrentUser();
        return incomeRepository
                .findByUserAndDateBetweenAndNameContainingIgnoreCase(user, startDate, endDate, keyword, sort)
                .stream()
                .map(i -> modelMapper.map(i, IncomeResponseDTO.class))
                .toList();
    }

    @Override
    public List<IncomeResponseDTO> getIncomesForUserOnDate(LocalDate date) {
        User user = getCurrentUser();
        return incomeRepository.findByUserAndDateBetween(user, date, date).stream()
                .map(i -> modelMapper.map(i, IncomeResponseDTO.class))
                .toList();
    }

    @Override
    public void deleteIncome(Long incomeId) {
        User user = getCurrentUser();
        Income income = incomeRepository.findById(incomeId)
                .orElseThrow(() -> new ApiException("Income not found"));

        if (!income.getUser().getId().equals(user.getId())) {
            throw new ApiException("You do not have permission to delete this income");
        }

        incomeRepository.delete(income);
    }
}
