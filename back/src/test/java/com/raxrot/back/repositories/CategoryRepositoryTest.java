package com.raxrot.back.repositories;

import com.raxrot.back.entities.Category;
import com.raxrot.back.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
@DataJpaTest
@ActiveProfiles("test")
class CategoryRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private User user;
    private Category category1;
    private Category category2;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .fullName("John Doe")
                .email("john@example.com")
                .password("securePassword")
                .build();

        userRepository.save(user);

        category1 = Category.builder()
                .name("Food")
                .type("EXPENSE")
                .icon("food-icon")
                .user(user)
                .build();

        category2 = Category.builder()
                .name("Transport")
                .type("EXPENSE")
                .icon("bus-icon")
                .user(user)
                .build();

        categoryRepository.save(category1);
        categoryRepository.save(category2);
    }

    @Test
    @DisplayName("Find categories by user")
    void findByUser() {
        List<Category> categories = categoryRepository.findByUser(user);

        assertThat(categories).hasSize(2);
        assertThat(categories).extracting(Category::getName)
                .containsExactlyInAnyOrder("Food", "Transport");
    }

    @Test
    @DisplayName("Find category by ID and user")
    void findByIdAndUser() {
        Optional<Category> result = categoryRepository.findByIdAndUser(category1.getId(), user);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Food");
    }

    @Test
    @DisplayName("Find categories by type and user")
    void findByTypeAndUser() {
        List<Category> categories = categoryRepository.findByTypeAndUser("EXPENSE", user);

        assertThat(categories).hasSize(2);
        assertThat(categories).allMatch(c -> c.getType().equals("EXPENSE") && c.getUser().equals(user));
    }

    @Test
    @DisplayName("Check if category exists by name and user")
    void existsByNameAndUser() {
        boolean exists = categoryRepository.existsByNameAndUser("Food", user);
        boolean notExists = categoryRepository.existsByNameAndUser("Health", user);

        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }
}
