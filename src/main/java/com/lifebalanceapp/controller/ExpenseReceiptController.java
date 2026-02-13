package com.lifebalanceapp.controller;

import com.lifebalanceapp.dto.ExpenseReceiptResponseDto;
import com.lifebalanceapp.model.ExpenseReceipt;
import com.lifebalanceapp.service.ExpenseReceiptService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ExpenseReceiptController {

    private final ExpenseReceiptService receiptService;

    public ExpenseReceiptController(ExpenseReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    private Integer requireUserId(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        return userId;
    }

    @PostMapping("/expenses/{expenseId}/receipts")
    public ResponseEntity<ExpenseReceiptResponseDto> upload(
            HttpSession session,
            @PathVariable Integer expenseId,
            @RequestParam("file") MultipartFile file
    ) {
        Integer userId = requireUserId(session);
        ExpenseReceiptResponseDto saved = receiptService.upload(userId, expenseId, file);
        return ResponseEntity.status(201).body(saved);
    }

    @GetMapping("/expenses/{expenseId}/receipts")
    public ResponseEntity<List<ExpenseReceiptResponseDto>> list(
            HttpSession session,
            @PathVariable Integer expenseId
    ) {
        Integer userId = requireUserId(session);
        return ResponseEntity.ok(receiptService.listByExpense(userId, expenseId));
    }

    @DeleteMapping("/receipts/{receiptId}")
    public ResponseEntity<Void> delete(
            HttpSession session,
            @PathVariable Integer receiptId
    ) {
        Integer userId = requireUserId(session);
        receiptService.delete(userId, receiptId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/receipts/{receiptId}/file")
    public ResponseEntity<org.springframework.core.io.Resource> download(
            HttpSession session,
            @PathVariable Integer receiptId
    ) {
        Integer userId = requireUserId(session);


        ExpenseReceipt receipt = receiptService.getReceiptForUser(userId, receiptId);

        java.nio.file.Path path = receiptService.resolvePhysicalPath(userId, receiptId);
        org.springframework.core.io.Resource resource = new org.springframework.core.io.FileSystemResource(path);

        if (!resource.exists()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found");
        }

        String contentType = receipt.getContentType();
        if (contentType == null || contentType.isBlank()) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(org.springframework.http.MediaType.parseMediaType(contentType))
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + receipt.getOriginalFilename() + "\"")
                .body(resource);
    }
}
