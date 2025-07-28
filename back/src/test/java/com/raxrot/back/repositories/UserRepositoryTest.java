package com.raxrot.back.repositories;

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
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .fullName("John Doe")
                .email("john@example.com")
                .password("securePassword")
                .profileImageUrl("http://image.url")
                .activationToken("abc123")
                .build();
    }

    @Test
    @DisplayName("Create user and check ID, timestamps, isActive")
    void createUser() {
        User savedUser = userRepository.save(user);

        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getCreatedAt()).isNotNull();
        assertThat(savedUser.getUpdatedAt()).isNotNull();
        assertThat(savedUser.getIsActive()).isFalse(); // проверка PrePersist
    }

    @Test
    @DisplayName("Find user by ID")
    void findUserById() {
        User savedUser = userRepository.save(user);
        Optional<User> userFromDb = userRepository.findById(savedUser.getId());

        assertThat(userFromDb).isPresent();
        assertThat(userFromDb.get().getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    @DisplayName("Find user by email")
    void findUserByEmail() {
        userRepository.save(user);
        Optional<User> result = userRepository.findByEmail("john@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getFullName()).isEqualTo("John Doe");
    }

    @Test
    @DisplayName("Get all users")
    void getAllUsers() {
        User user2 = User.builder()
                .fullName("Jane Doe")
                .email("jane@example.com")
                .password("anotherPassword")
                .build();

        userRepository.save(user);
        userRepository.save(user2);

        List<User> users = userRepository.findAll();

        assertThat(users).hasSize(2)
                .extracting(User::getEmail)
                .containsExactlyInAnyOrder("john@example.com", "jane@example.com");
    }

    @Test
    @DisplayName("Update user")
    void updateUser() {
        User savedUser = userRepository.save(user);
        savedUser.setFullName("Updated Name");

        User updatedUser = userRepository.save(savedUser);

        assertThat(updatedUser.getFullName()).isEqualTo("Updated Name");
        assertThat(updatedUser.getUpdatedAt()).isAfterOrEqualTo(updatedUser.getCreatedAt());
    }

    @Test
    @DisplayName("Delete user")
    void deleteUser() {
        User savedUser = userRepository.save(user);
        userRepository.delete(savedUser);

        Optional<User> deletedUser = userRepository.findById(savedUser.getId());

        assertThat(deletedUser).isNotPresent();
        assertThat(userRepository.existsById(savedUser.getId())).isFalse();
    }

    @Test
    @DisplayName("User not found by email")
    void userNotFoundByEmail() {
        Optional<User> result = userRepository.findByEmail("notfound@example.com");

        assertThat(result).isNotPresent();
    }

    @Test
    @DisplayName("Find user by activation token")
    void findUserByActivationToken() {
        userRepository.save(user);

        Optional<User> result = userRepository.findByActivationToken("abc123");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("john@example.com");
    }
}