package api.medpulse.uz.controller;

import api.medpulse.uz.entity.AdminActionLogEntity;
import api.medpulse.uz.repository.AdminActionLogRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin-logs")
@RequiredArgsConstructor
@Tag(name = "Admin Logs", description = "Tizimdagi barcha admin harakatlari (Audit)")
public class AdminLogController {

    private final AdminActionLogRepository logRepository;

    // Faqat SUPERADMIN ko'ra oladi
    @PreAuthorize("hasRole('SUPERADMIN')")
    @GetMapping
    @Operation(summary = "Loglarni ko'rish", description = "Pagination orqali loglarni olish")
    public ResponseEntity<Page<AdminActionLogEntity>> getAllLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(logRepository.findAllByOrderByCreatedDateDesc(pageable));
    }
}
