package com.lifebalanceapp.service;

import com.lifebalanceapp.dto.AuthResponseDto;
import com.lifebalanceapp.dto.ProfileUpdateRequestDto;
import com.lifebalanceapp.exception.NotFoundException;
import com.lifebalanceapp.model.User;
import com.lifebalanceapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Set;

@Service
public class ProfileService {

    public record AvatarFile(Path path, String contentType, String filename) {}

    private final UserRepository userRepository;
    private final Path uploadRoot;

    private static final Set<String> ALLOWED = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    public ProfileService(
            UserRepository userRepository,
            @Value("${app.upload-dir:uploads}") String uploadDir
    ) {
        this.userRepository = userRepository;
        this.uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @Transactional
    public AuthResponseDto update(Integer userId, ProfileUpdateRequestDto req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (req.getFullName() != null) user.setFullName(req.getFullName());
        if (req.getAbout() != null) user.setAbout(req.getAbout());

        User saved = userRepository.save(user);
        return toAuthDto(saved);
    }

    @Transactional
    public void uploadAvatar(Integer userId, MultipartFile file) {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("File is required");

        String ct = file.getContentType();
        if (ct == null || !ALLOWED.contains(ct)) {
            throw new IllegalArgumentException("Only jpg/png/webp allowed");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        // uploads/avatars/<userId>/
        Path dir = uploadRoot.resolve("avatars").resolve(String.valueOf(userId));
        try {
            Files.createDirectories(dir);
        } catch (Exception e) {
            throw new RuntimeException("Could not create upload directory", e);
        }

        String ext = switch (ct) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> "";
        };

        Path target = dir.resolve("avatar" + ext).normalize();

        // dacă exista avatar vechi, îl ștergem (best effort)
        if (user.getAvatarPath() != null && !user.getAvatarPath().isBlank()) {
            try {
                Files.deleteIfExists(uploadRoot.resolve(user.getAvatarPath()).normalize());
            } catch (Exception ignored) {}
        }

        try {
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            throw new RuntimeException("Could not save file", e);
        }

        String rel = uploadRoot.relativize(target).toString().replace("\\", "/");
        user.setAvatarPath(rel);
        userRepository.save(user);
    }

    @Transactional
    public AvatarFile getAvatarFile(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (user.getAvatarPath() == null || user.getAvatarPath().isBlank()) return null;

        Path path = uploadRoot.resolve(user.getAvatarPath()).normalize();

        String contentType = "application/octet-stream";
        try {
            String probed = Files.probeContentType(path);
            if (probed != null && !probed.isBlank()) contentType = probed;
        } catch (Exception ignored) {}

        return new AvatarFile(path, contentType, path.getFileName().toString());
    }

    @Transactional
    public void deleteAvatar(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (user.getAvatarPath() != null && !user.getAvatarPath().isBlank()) {
            try {
                Files.deleteIfExists(uploadRoot.resolve(user.getAvatarPath()).normalize());
            } catch (Exception ignored) {}

            user.setAvatarPath(null);
            userRepository.save(user);
        }
    }

    private AuthResponseDto toAuthDto(User u) {
        AuthResponseDto dto = new AuthResponseDto();
        dto.setUserId(u.getId());
        dto.setFullName(u.getFullName());
        dto.setEmail(u.getEmail());
        dto.setAbout(u.getAbout());

        // IMPORTANT: frontend folosește asta ca <img src="API_BASE + avatarUrl">
        dto.setAvatarUrl(u.getAvatarPath() == null ? null : "/profile/avatar");

        return dto;
    }
}
