package com.example.Right.config;

import com.example.Right.model.Role;
import com.example.Right.model.User;
import com.example.Right.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByEmail("admin_kush@gmail.com").isEmpty()) {
            User admin = User.builder()
                    .name("Kush Admin")
                    .email("admin_kush@gmail.com")
                    .password(passwordEncoder.encode("admin@kush"))
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(admin);
            System.out.println("Admin user created: admin_kush@gmail.com");
        }
    }
}
