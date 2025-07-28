package com.raxrot.back.repositories;

import com.raxrot.back.entities.Category;
import com.raxrot.back.entities.Expense;
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
class ExpenseRepositoryTest {

    @Autowired
    private ExpenseRepository expenseRepository;

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
                .name("Groceries")
                .type("EXPENSE")
                .icon("icon-groceries")
                .user(user)
                .build();
        categoryRepository.save(category);

        for (int i = 1; i <= 4; i++) {
            Expense expense = Expense.builder()
                    .name("Expense " + i)
                    .amount(BigDecimal.valueOf(50 * i))
                    .icon("icon")
                    .user(user)
                    .category(category)
                    .build();
            expenseRepository.save(expense);
        }
    }

    @Test
    @DisplayName("Find all expenses by user")
    void findByUser() {
        List<Expense> expenses = expenseRepository.findByUser(user);
        assertThat(expenses).hasSize(4);
    }

    @Test
    @DisplayName("Find top 5 expenses by user ordered by date desc")
    void findTop5ByUserOrderByDateDesc() {
        List<Expense> latestExpenses = expenseRepository.findTop5ByUserOrderByDateDesc(user);
        assertThat(latestExpenses).hasSize(4); // всего 4
        assertThat(latestExpenses.get(0).getDate()).isAfterOrEqualTo(latestExpenses.get(1).getDate());
    }

    @Test
    @DisplayName("Get total expense amount by user")
    void getTotalAmountByUser() {
        BigDecimal total = expenseRepository.getTotalAmountByUser(user);
        assertThat(total).isEqualByComparingTo("500"); // 50+100+150+200
    }

    @Test
    @DisplayName("Find expenses by user and date range")
    void findByUserAndDateBetween() {
        LocalDate start = LocalDate.now().minusDays(10);
        LocalDate end = LocalDate.now().plusDays(1);

        List<Expense> expenses = expenseRepository.findByUserAndDateBetween(user, start, end);

        assertThat(expenses).hasSize(4);
    }

    @Test
    @DisplayName("Find expenses by user, date range and name keyword (case-insensitive)")
    void findByUserAndDateBetweenAndNameContainingIgnoreCase() {
        LocalDate start = LocalDate.now().minusDays(10);
        LocalDate end = LocalDate.now().plusDays(1);
        Sort sort = Sort.by(Sort.Direction.DESC, "date");

        List<Expense> expenses = expenseRepository.findByUserAndDateBetweenAndNameContainingIgnoreCase(
                user, start, end, "expense", sort
        );

        assertThat(expenses).hasSize(4);
        assertThat(expenses.get(0).getDate()).isAfterOrEqualTo(expenses.get(1).getDate());
    }

}
