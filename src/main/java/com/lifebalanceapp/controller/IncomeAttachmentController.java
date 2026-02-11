package com.lifebalanceapp.controller;

import com.lifebalanceapp.dto.IncomeAttachmentResponseDto;
import com.lifebalanceapp.model.IncomeAttachment;
import com.lifebalanceapp.service.IncomeAttachmentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/api")
public class IncomeAttachmentController {

    private final IncomeAttachmentService service;

    public IncomeAttachmentController(IncomeAttachmentService service) {
        this.service = service;
    }

    private Integer requireUserId(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        return userId;
    }

    @PostMapping("/incomes/{incomeId}/attachments")
    public ResponseEntity<IncomeAttachmentResponseDto> upload(
            HttpSession session,
            @PathVariable Integer incomeId,
            @RequestParam("file") MultipartFile file
    ) {
        Integer userId = requireUserId(session);
        return ResponseEntity.status(201).body(service.upload(userId, incomeId, file));
    }

    @GetMapping("/incomes/{incomeId}/attachments")
    public ResponseEntity<List<IncomeAttachmentResponseDto>> list(
            HttpSession session,
            @PathVariable Integer incomeId
    ) {
        Integer userId = requireUserId(session);
        return ResponseEntity.ok(service.listByIncome(userId, incomeId));
    }

    @DeleteMapping("/income-attachments/{attachmentId}")
    public ResponseEntity<Void> delete(
            HttpSession session,
            @PathVariable Integer attachmentId
    ) {
        Integer userId = requireUserId(session);
        service.delete(userId, attachmentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/income-attachments/{attachmentId}/file")
    public ResponseEntity<org.springframework.core.io.Resource> file(
            HttpSession session,
            @PathVariable Integer attachmentId
    ) {
        Integer userId = requireUserId(session);

        IncomeAttachment a = service.getForUser(userId, attachmentId);
        Path path = service.resolvePhysicalPath(userId, attachmentId);

      Resource resource = new FileSystemResource(path);
        if (!resource.exists()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found");

        String contentType = a.getContentType();
        if (contentType == null || contentType.isBlank()) contentType = "application/octet-stream";

        return ResponseEntity.ok()
                .contentType(org.springframework.http.MediaType.parseMediaType(contentType))
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + a.getOriginalFilename() + "\"")
                .body(resource);
    }
}
