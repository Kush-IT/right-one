package com.example.Right.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOtpEmail(String to, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("RIGHTONE <noreply@rightone.com>");
            message.setTo(to);
            message.setSubject("Your Login OTP - RIGHTONE");
            message.setText(
                    "Your One-Time Password (OTP) for login is: " + otp + "\n\nThis OTP is valid for 5 minutes.");

            mailSender.send(message);
            log.info("OTP email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error(
                    "Failed to send OTP email to {}. Error: {}. Ensure properties are correctly configured. OTP is: {}",
                    to, e.getMessage(), otp);
        }
    }
}
