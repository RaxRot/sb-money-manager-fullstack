package com.raxrot.back.repositories;

import com.raxrot.back.entities.Category;
import com.raxrot.back.entities.Income;
import com.raxrot.back.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class IncomeRepositoryTest {

    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private User user;
    private Category category;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .fullName("John Doe")
                .email("john@example.com")
                .password("securePassword")
                .build();
        userRepository.save(user);

        category = Category.builder()
                .name("Salary")
                .type("INCOME")
                .icon("icon-salary")
                .user(user)
                .build();
        categoryRepository.save(category);

        for (int i = 1; i <= 6; i++) {
            Income income = Income.builder()
                    .name("Income " + i)
                    .amount(BigDecimal.valueOf(100 * i))
                    .icon("icon")
                    .user(user)
                    .category(category)
                    .build();
            incomeRepository.save(income);
        }
    }

    @Test
    @DisplayName("Find all incomes by user")
    void findByUser() {
        List<Income> incomes = incomeRepository.findByUser(user);
        assertThat(incomes).hasSize(6);
    }

    @Test
    @DisplayName("Find top 5 incomes by user ordered by date desc")
    void findTop5ByUserOrderByDateDesc() {
        List<Income> latestIncomes = incomeRepository.findTop5ByUserOrderByDateDesc(user);
        assertThat(latestIncomes).hasSize(5);
        assertThat(latestIncomes.get(0).getDate()).isAfterOrEqualTo(latestIncomes.get(1).getDate());
    }

    @Test
    @DisplayName("Get total income amount by user")
    void getTotalAmountByUser() {
        BigDecimal total = incomeRepository.getTotalAmountByUser(user);
        assertThat(total).isEqualByComparingTo("2100"); // 100+200+300+400+500+600
    }

    @Test
    @DisplayName("Find incomes by user and date range")
    void findByUserAndDateBetween() {
        LocalDate start = LocalDate.now().minusDays(10);
        LocalDate end = LocalDate.now().plusDays(1);

        List<Income> incomes = incomeRepository.findByUserAndDateBetween(user, start, end);

        assertThat(incomes).hasSize(6);
    }

    @Test
    @DisplayName("Find incomes by user, date range and name keyword (case-insensitive)")
    void findByUserAndDateBetweenAndNameContainingIgnoreCase() {
        LocalDate start = LocalDate.now().minusDays(10);
        LocalDate end = LocalDate.now().plusDays(1);
        Sort sort = Sort.by(Sort.Direction.DESC, "date");

        List<Income> incomes = incomeRepository.findByUserAndDateBetweenAndNameContainingIgnoreCase(
                user, start, end, "income", sort
        );

        assertThat(incomes).hasSize(6);
        assertThat(incomes.get(0).getDate()).isAfterOrEqualTo(incomes.get(1).getDate());
    }

}
