package com.example.Right.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class EmailService {

    @Value("${google.script.url:}")
    private String scriptUrl;

    public void sendOtpEmail(String to, String otp) {
        if (scriptUrl == null || scriptUrl.isEmpty()) {
            log.error("Google Script URL is not configured. Cannot send OTP to {}", to);
            return;
        }

        try {
            RestTemplate restTemplate = new RestTemplate();
            Map<String, String> data = new HashMap<>();
            data.put("to", to);
            data.put("subject", "Your Login OTP - RIGHTONE");
            data.put("text", "Your One-Time Password (OTP) for login is: " + otp + "\n\nThis OTP is valid for 5 minutes.");

            restTemplate.postForEntity(scriptUrl, data, String.class);
            log.info("OTP email request sent via Google Script to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send OTP email via Google Script to {}. Error: {}", to, e.getMessage());
        }
    }
}
