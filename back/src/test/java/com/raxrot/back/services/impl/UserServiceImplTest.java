package com.raxrot.back.services.impl;

import com.raxrot.back.dtos.UserRequestDTO;
import com.raxrot.back.dtos.UserResponseDTO;
import com.raxrot.back.entities.User;
import com.raxrot.back.exceptions.ApiException;
import com.raxrot.back.repositories.UserRepository;
import com.raxrot.back.services.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRequestDTO requestDTO;
    private UserResponseDTO responseDTO;
    private User user;

    @BeforeEach
    void setUp() {
        requestDTO = UserRequestDTO.builder()
                .fullName("John Doe")
                .email("john@example.com")
                .password("123456")
                .build();

        user = User.builder()
                .id(1L)
                .fullName("John Doe")
                .email("john@example.com")
                .password("123456")
                .isActive(false)
                .build();

        responseDTO = UserResponseDTO.builder()
                .id(1L)
                .fullName("John Doe")
                .email("john@example.com")
                .build();
    }

    @Test
    @DisplayName("Create user successfully and send activation email")
    void createUser_success() {
        // given
        given(userRepository.findByEmail("john@example.com")).willReturn(Optional.empty());
        given(modelMapper.map(requestDTO, User.class)).willReturn(user);
        given(userRepository.save(user)).willReturn(user);
        given(modelMapper.map(user, UserResponseDTO.class)).willReturn(responseDTO);

        // when
        UserResponseDTO createdUser = userService.createUser(requestDTO);

        // then
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getEmail()).isEqualTo("john@example.com");

        then(userRepository).should().save(user);
        then(emailService).should().sendEmail(
                eq("john@example.com"),
                eq("Activate Money Manager Account"),
                contains("http://localhost:8080/api/v1.0/activate?token=")
        );

        assertThat(user.getActivationToken()).isNotNull();
    }

    @Test
    @DisplayName("Throw exception if email already exists")
    void createUser_emailAlreadyExists() {
        // given
        given(userRepository.findByEmail("john@example.com")).willReturn(Optional.of(user));

        // when / then
        assertThatThrownBy(() -> userService.createUser(requestDTO))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Email address already in use");

        then(userRepository).should(never()).save(any());
        then(emailService).should(never()).sendEmail(any(), any(), any());
    }

    @Test
    @DisplayName("Activate user with valid token")
    void activateUser_success() {
        // given
        String token = UUID.randomUUID().toString();
        user.setActivationToken(token);
        user.setIsActive(false);

        given(userRepository.findByActivationToken(token)).willReturn(Optional.of(user));
        given(userRepository.save(user)).willReturn(user);

        // when
        boolean result = userService.activateUser(token);

        // then
        assertThat(result).isTrue();
        assertThat(user.getIsActive()).isTrue();

        then(userRepository).should().save(user);
    }

    @Test
    @DisplayName("Fail to activate user with invalid token")
    void activateUser_invalidToken() {
        // given
        String invalidToken = "invalid-token";
        given(userRepository.findByActivationToken(invalidToken)).willReturn(Optional.empty());

        // when
        boolean result = userService.activateUser(invalidToken);

        // then
        assertThat(result).isFalse();
        then(userRepository).should(never()).save(any());
    }
}