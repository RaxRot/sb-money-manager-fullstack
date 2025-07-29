package com.raxrot.back.services.impl;

import com.raxrot.back.exceptions.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailServiceImplTest {
    private JavaMailSender mailSender;
    private EmailServiceImpl emailService;

    @BeforeEach
    void setUp() throws Exception {
        mailSender = mock(JavaMailSender.class);
        emailService = new EmailServiceImpl(mailSender);

        // Устанавливаем значение fromEmail вручную
        Field fromEmailField = EmailServiceImpl.class.getDeclaredField("fromEmail");
        fromEmailField.setAccessible(true);
        fromEmailField.set(emailService, "noreply@example.com");
    }

    @Test
    void testSendEmail_success() {
        emailService.sendEmail("to@example.com", "Hello", "Test body");

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(captor.capture());

        SimpleMailMessage sent = captor.getValue();
        assertEquals("noreply@example.com", sent.getFrom());
        assertEquals("to@example.com", sent.getTo()[0]);
        assertEquals("Hello", sent.getSubject());
        assertEquals("Test body", sent.getText());
    }

    @Test
    void testSendEmail_failure() {
        doThrow(new RuntimeException("SMTP fail")).when(mailSender).send(any(SimpleMailMessage.class));

        ApiException ex = assertThrows(ApiException.class, () ->
                emailService.sendEmail("to@example.com", "Subject", "Body"));

        assertTrue(ex.getMessage().contains("Error sending email: SMTP fail"));
    }
}