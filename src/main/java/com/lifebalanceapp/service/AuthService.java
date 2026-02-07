package com.lifebalanceapp.service;

import com.lifebalanceapp.dto.AuthResponseDto;
import com.lifebalanceapp.dto.LoginRequestDto;
import com.lifebalanceapp.dto.RegisterRequestDto;
import com.lifebalanceapp.exception.NotFoundException;
import com.lifebalanceapp.model.User;
import com.lifebalanceapp.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponseDto register(RegisterRequestDto request) {
        userRepository.findByEmail(request.getEmail()).ifPresent(u -> {
            throw new IllegalArgumentException("Email already used");
        });

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setAbout(request.getAbout());

        User saved = userRepository.save(user);
        return toAuthDto(saved);
    }

    public AuthResponseDto login(LoginRequestDto request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException("Invalid credentials"));

        boolean ok = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!ok) throw new NotFoundException("Invalid credentials");

        return toAuthDto(user);
    }

    public AuthResponseDto me(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        return toAuthDto(user);
    }

    private AuthResponseDto toAuthDto(User user) {
        AuthResponseDto dto = new AuthResponseDto();
        dto.setUserId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setAbout(user.getAbout());

        // IMPORTANT:
        // - API_BASE pe frontend e "/api"
        // - controller-ul e "/api/profile/avatar"
        // deci aici trimitem doar "/profile/avatar"
        dto.setAvatarUrl(user.getAvatarPath() == null || user.getAvatarPath().isBlank()
                ? null
                : "/profile/avatar");

        return dto;
    }
}
