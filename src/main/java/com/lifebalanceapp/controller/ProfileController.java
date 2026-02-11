package com.lifebalanceapp.controller;

import com.lifebalanceapp.dto.AuthResponseDto;
import com.lifebalanceapp.dto.ProfileUpdateRequestDto;
import com.lifebalanceapp.service.ProfileService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    private Integer requireUserId(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        return userId;
    }

    @PutMapping
    public ResponseEntity<AuthResponseDto> update(
            HttpSession session,
            @RequestBody @Valid ProfileUpdateRequestDto request
    ) {
        Integer userId = requireUserId(session);
        return ResponseEntity.ok(profileService.update(userId, request));
    }

    @PostMapping("/avatar")
    public ResponseEntity<Void> uploadAvatar(
            HttpSession session,
            @RequestParam("file") MultipartFile file
    ) {
        Integer userId = requireUserId(session);
        profileService.uploadAvatar(userId, file);
        return ResponseEntity.status(201).build();
    }

    @GetMapping("/avatar")
    public ResponseEntity<Resource> viewAvatar(HttpSession session) {
        Integer userId = requireUserId(session);

        ProfileService.AvatarFile af = profileService.getAvatarFile(userId);
        if (af == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No avatar");

        Resource resource = new FileSystemResource(af.path());
        if (!resource.exists()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found");

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(af.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + af.filename() + "\"")
                .body(resource);
    }

    @DeleteMapping("/avatar")
    public ResponseEntity<Void> deleteAvatar(HttpSession session) {
        Integer userId = requireUserId(session);
        profileService.deleteAvatar(userId);
        return ResponseEntity.noContent().build();
    }
}
