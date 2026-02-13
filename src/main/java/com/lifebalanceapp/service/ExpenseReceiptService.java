package com.lifebalanceapp.service;

import com.lifebalanceapp.dto.ExpenseReceiptResponseDto;
import com.lifebalanceapp.exception.NotFoundException;
import com.lifebalanceapp.model.Expense;
import com.lifebalanceapp.model.ExpenseReceipt;
import com.lifebalanceapp.repository.ExpenseReceiptRepository;
import com.lifebalanceapp.repository.ExpenseRepository;
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
public class ExpenseReceiptService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseReceiptRepository receiptRepository;
    private final Path uploadRoot;

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "application/pdf"
    );

    public ExpenseReceiptService(
            ExpenseRepository expenseRepository,
            ExpenseReceiptRepository receiptRepository,
            @Value("${app.upload-dir:uploads}") String uploadDir
    ) {
        this.expenseRepository = expenseRepository;
        this.receiptRepository = receiptRepository;
        this.uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @Transactional
    public ExpenseReceiptResponseDto upload(Integer userId, Integer expenseId, MultipartFile file) {
        validateFile(file);

        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new NotFoundException("Expense not found"));

        if (!expense.getUser().getId().equals(userId)) {
            throw new NotFoundException("Expense not found");
        }

        Path expenseDir = uploadRoot
                .resolve("receipts")
                .resolve(String.valueOf(userId))
                .resolve(String.valueOf(expenseId));

        try {
            Files.createDirectories(expenseDir);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory", e);
        }

        String originalName = safeFilename(file.getOriginalFilename());
        String ext = guessExtension(originalName, file.getContentType());
        String storedName = UUID.randomUUID().toString() + ext;

        Path targetPath = expenseDir.resolve(storedName).normalize();

        try {
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Could not save file", e);
        }

        ExpenseReceipt receipt = new ExpenseReceipt();
        receipt.setExpense(expense);

        Path relativePath = uploadRoot.relativize(targetPath);
        receipt.setFilePath(relativePath.toString().replace("\\", "/"));

        receipt.setOriginalFilename(originalName);
        receipt.setContentType(file.getContentType());
        receipt.setFileSize(file.getSize());
        receipt.setUploadedAt(LocalDateTime.now());

        return toDto(receiptRepository.save(receipt));
    }

    @Transactional
    public void delete(Integer userId, Integer receiptId) {
        // IMPORTANT: folosim fetch join ca să nu crape pe LAZY
        ExpenseReceipt receipt = receiptRepository.findByIdWithExpenseAndUser(receiptId)
                .orElseThrow(() -> new NotFoundException("Receipt not found"));

        if (!receipt.getExpense().getUser().getId().equals(userId)) {
            throw new NotFoundException("Receipt not found");
        }

        Path physical = uploadRoot.resolve(receipt.getFilePath()).normalize();
        try {
            Files.deleteIfExists(physical);
        } catch (IOException ignored) {}

        receiptRepository.delete(receipt);
    }

    @Transactional
    public List<ExpenseReceiptResponseDto> listByExpense(Integer userId, Integer expenseId) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new NotFoundException("Expense not found"));

        if (!expense.getUser().getId().equals(userId)) {
            throw new NotFoundException("Expense not found");
        }

        return receiptRepository.findByExpense_Id(expenseId)
                .stream()
                .map(this::toDto)
                .toList();
    }


    @Transactional
    public ExpenseReceipt getReceiptForUser(Integer userId, Integer receiptId) {
        ExpenseReceipt receipt = receiptRepository.findByIdWithExpenseAndUser(receiptId)
                .orElseThrow(() -> new NotFoundException("Receipt not found"));

        if (!receipt.getExpense().getUser().getId().equals(userId)) {
            throw new NotFoundException("Receipt not found");
        }
        return receipt;
    }


    @Transactional
    public Path resolvePhysicalPath(Integer userId, Integer receiptId) {
        ExpenseReceipt receipt = getReceiptForUser(userId, receiptId);
        return uploadRoot.resolve(receipt.getFilePath()).normalize();
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }
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
        if (dot >= 0 && dot < originalName.length() - 1) {
            return originalName.substring(dot).toLowerCase();
        }
        if ("image/jpeg".equals(contentType)) return ".jpg";
        if ("image/png".equals(contentType)) return ".png";
        if ("application/pdf".equals(contentType)) return ".pdf";
        return "";
    }

    private ExpenseReceiptResponseDto toDto(ExpenseReceipt r) {
        ExpenseReceiptResponseDto dto = new ExpenseReceiptResponseDto();
        dto.setId(r.getId());
        dto.setExpenseId(r.getExpense().getId());
        dto.setFilePath(r.getFilePath());
        dto.setOriginalFilename(r.getOriginalFilename());
        dto.setContentType(r.getContentType());
        dto.setFileSize(r.getFileSize());
        dto.setUploadedAt(r.getUploadedAt());
        return dto;
    }
}
