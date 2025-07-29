package com.raxrot.back.services.impl;

import com.raxrot.back.entities.User;
import com.raxrot.back.repositories.UserRepository;
import com.raxrot.back.services.EmailService;
import com.raxrot.back.services.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Scheduled(cron ="0 0 22 * * *",zone ="Europe/Lisbon")
    @Override
    public void sendDailyIncomeExpenseReminder() {
        List<User>users=userRepository.findAll();
        for (User user : users) {
            try {
                String body = "Hi " + user.getFullName() + ",\nPlease add income/expense to your account.";
                emailService.sendEmail(user.getEmail(), "Add Income Expense", body);
                log.info("Email sent to {}", user.getEmail());
            } catch (Exception e) {
                log.error("Failed to send email to {}: {}", user.getEmail(), e.getMessage());
            }
        }
    }
}
