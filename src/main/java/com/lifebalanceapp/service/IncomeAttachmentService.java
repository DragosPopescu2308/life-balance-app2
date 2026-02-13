package com.lifebalanceapp.service;

import com.lifebalanceapp.dto.IncomeAttachmentResponseDto;
import com.lifebalanceapp.exception.NotFoundException;
import com.lifebalanceapp.model.Income;
import com.lifebalanceapp.model.IncomeAttachment;
import com.lifebalanceapp.repository.IncomeAttachmentRepository;
import com.lifebalanceapp.repository.IncomeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class IncomeAttachmentService {

    private final IncomeRepository incomeRepository;
    private final IncomeAttachmentRepository attachmentRepository;
    private final Path uploadRoot;

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "application/pdf"
    );

    public IncomeAttachmentService(
            IncomeRepository incomeRepository,
            IncomeAttachmentRepository attachmentRepository,
            @Value("${app.upload-dir:uploads}") String uploadDir
    ) {
        this.incomeRepository = incomeRepository;
        this.attachmentRepository = attachmentRepository;
        this.uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @Transactional
    public IncomeAttachmentResponseDto upload(Integer userId, Integer incomeId, MultipartFile file) {
        validateFile(file);

        Income income = incomeRepository.findById(incomeId)
                .orElseThrow(() -> new NotFoundException("Income not found"));

        if (!income.getUser().getId().equals(userId)) {
            throw new NotFoundException("Income not found");
        }

        Path incomeDir = uploadRoot
                .resolve("income-attachments")
                .resolve(String.valueOf(userId))
                .resolve(String.valueOf(incomeId));

        try {
            Files.createDirectories(incomeDir);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory", e);
        }

        String originalName = safeFilename(file.getOriginalFilename());
        String ext = guessExtension(originalName, file.getContentType());
        String storedName = UUID.randomUUID().toString() + ext;

        Path targetPath = incomeDir.resolve(storedName).normalize();

        try {
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Could not save file", e);
        }

        IncomeAttachment a = new IncomeAttachment();
        a.setIncome(income);

        Path relativePath = uploadRoot.relativize(targetPath);
        a.setFilePath(relativePath.toString().replace("\\", "/"));

        a.setOriginalFilename(originalName);
        a.setContentType(file.getContentType());
        a.setFileSize(file.getSize());
        a.setUploadedAt(LocalDateTime.now());

        return toDto(attachmentRepository.save(a));
    }

    @Transactional
    public List<IncomeAttachmentResponseDto> listByIncome(Integer userId, Integer incomeId) {
        Income income = incomeRepository.findById(incomeId)
                .orElseThrow(() -> new NotFoundException("Income not found"));

        if (!income.getUser().getId().equals(userId)) {
            throw new NotFoundException("Income not found");
        }

        return attachmentRepository.findByIncome_Id(incomeId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public void delete(Integer userId, Integer attachmentId) {
        IncomeAttachment a = attachmentRepository.findByIdWithIncomeAndUser(attachmentId)
                .orElseThrow(() -> new NotFoundException("Attachment not found"));

        if (!a.getIncome().getUser().getId().equals(userId)) {
            throw new NotFoundException("Attachment not found");
        }

        Path physical = uploadRoot.resolve(a.getFilePath()).normalize();
        try { Files.deleteIfExists(physical); } catch (IOException ignored) {}

        attachmentRepository.delete(a);
    }

    @Transactional
    public IncomeAttachment getForUser(Integer userId, Integer attachmentId) {
        IncomeAttachment a = attachmentRepository.findByIdWithIncomeAndUser(attachmentId)
                .orElseThrow(() -> new NotFoundException("Attachment not found"));

        if (!a.getIncome().getUser().getId().equals(userId)) {
            throw new NotFoundException("Attachment not found");
        }
        return a;
    }

    @Transactional
    public Path resolvePhysicalPath(Integer userId, Integer attachmentId) {
        IncomeAttachment a = getForUser(userId, attachmentId);
        return uploadRoot.resolve(a.getFilePath()).normalize();
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("File is required");
        String ct = file.getContentType();
        if (ct == null || !ALLOWED_CONTENT_TYPES.contains(ct)) {
            throw new IllegalArgumentException("Unsupported file type. Allowed: jpg, png, pdf");
        }
    }

    private String safeFilename(String name) {
        if (name == null || name.isBlank()) return "file";
        name = name.replace("\\", "/");
        int idx = name.lastIndexOf("/");
        if (idx >= 0) name = name.substring(idx + 1);
        name = name.replaceAll("[^a-zA-Z0-9._-]", "_");
        if (name.length() > 200) name = name.substring(name.length() - 200);
        return name;
    }

    private String guessExtension(String originalName, String contentType) {
        int dot = originalName.lastIndexOf('.');
        if (dot >= 0 && dot < originalName.length() - 1) return originalName.substring(dot).toLowerCase();
        if ("image/jpeg".equals(contentType)) return ".jpg";
        if ("image/png".equals(contentType)) return ".png";
        if ("application/pdf".equals(contentType)) return ".pdf";
        return "";
    }

    private IncomeAttachmentResponseDto toDto(IncomeAttachment a) {
        IncomeAttachmentResponseDto dto = new IncomeAttachmentResponseDto();
        dto.setId(a.getId());
        dto.setIncomeId(a.getIncome().getId());
        dto.setFilePath(a.getFilePath());
        dto.setOriginalFilename(a.getOriginalFilename());
        dto.setContentType(a.getContentType());
        dto.setFileSize(a.getFileSize());
        dto.setUploadedAt(a.getUploadedAt());
        return dto;
    }
}
