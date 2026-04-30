package com.example.Right.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendOtpEmail(String to, String otp) {
        try {
            log.info("Attempting to send OTP email to: {} from: {}", to, fromEmail);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, "RIGHTONE");
            helper.setTo(to);
            helper.setSubject("Your Login OTP - RIGHTONE");

            String htmlContent = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: auto;'>"
                    + "<h2 style='color: #4f46e5;'>RIGHTONE - One-Time Password</h2>"
                    + "<p>Your OTP for login is:</p>"
                    + "<h1 style='letter-spacing: 8px; color: #1e293b;'>" + otp + "</h1>"
                    + "<p style='color: #64748b;'>This OTP is valid for <strong>5 minutes</strong>. Do not share it with anyone.</p>"
                    + "<hr style='border-color: #e2e8f0;'/>"
                    + "<small style='color: #94a3b8;'>If you did not request this OTP, please ignore this email.</small>"
                    + "</div>";

            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("OTP email sent successfully to: {}", to);

        } catch (Exception e) {
            log.error("Failed to send OTP email to: {}. Error type: {}. Message: {}",
                    to, e.getClass().getName(), e.getMessage(), e);
            // Don't rethrow — login still works, user just won't get email
        }
    }
}
