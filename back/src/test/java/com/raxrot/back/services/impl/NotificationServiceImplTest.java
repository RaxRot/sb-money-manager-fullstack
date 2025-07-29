package com.raxrot.back.services.impl;

import com.raxrot.back.entities.User;
import com.raxrot.back.repositories.UserRepository;
import com.raxrot.back.services.EmailService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Test
    void sendDailyIncomeExpenseReminder_sendsEmailToAllUsers() {

        User user1 = User.builder().email("user1@example.com").fullName("User One").build();
        User user2 = User.builder().email("user2@example.com").fullName("User Two").build();
        List<User> users = List.of(user1, user2);

        Mockito.when(userRepository.findAll()).thenReturn(users);

        notificationService.sendDailyIncomeExpenseReminder();

        Mockito.verify(emailService).sendEmail(
                Mockito.eq("user1@example.com"),
                Mockito.eq("Add Income Expense"),
                Mockito.contains("Hi User One")
        );
        Mockito.verify(emailService).sendEmail(
                Mockito.eq("user2@example.com"),
                Mockito.eq("Add Income Expense"),
                Mockito.contains("Hi User Two")
        );

        Mockito.verify(emailService, Mockito.times(2))
                .sendEmail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    }

    @Test
    void sendDailyIncomeExpenseReminder_handlesEmailException() {
        User user = User.builder().email("user@example.com").fullName("User").build();
        Mockito.when(userRepository.findAll()).thenReturn(List.of(user));

        Mockito.doThrow(new RuntimeException("Email service down"))
                .when(emailService).sendEmail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

        Assertions.assertDoesNotThrow(() -> notificationService.sendDailyIncomeExpenseReminder());

        Mockito.verify(emailService, Mockito.times(1))
                .sendEmail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    }
}
