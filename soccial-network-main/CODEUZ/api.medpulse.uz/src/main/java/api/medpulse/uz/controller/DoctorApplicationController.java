package api.medpulse.uz.controller;

import api.medpulse.uz.dto.doctor.DoctorApplyDTO;
import api.medpulse.uz.dto.doctor.DoctorFullDTO;
import api.medpulse.uz.dto.doctor.DoctorPublicDTO;
import api.medpulse.uz.enums.ApplicationStatus;
import api.medpulse.uz.service.DoctorDetailsService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/doctor-apply")
@RequiredArgsConstructor
public class DoctorApplicationController {

    private final DoctorDetailsService doctorDetailsService;

    // 1. Ariza yuborish (User)
    @PostMapping
    public ResponseEntity<String> apply(@Valid @RequestBody DoctorApplyDTO dto) {
        return ResponseEntity.ok(doctorDetailsService.apply(dto));
    }

    // 2. Statusni o'zgartirish (Admin)
    // URL: /api/v1/doctor-apply/change-status/15?status=APPROVED
    @PutMapping("/change-status/{id}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    @Operation(summary = "Admin only", description = "Arizani tasdiqlash yoki rad etish")
    public ResponseEntity<String> changeStatus(@PathVariable Long id,
                                               @RequestParam ApplicationStatus status,
                                               @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(doctorDetailsService.changeStatus(id, status, reason));
    }

    // 3. Admin uchun batafsil ko'rish
    @GetMapping("/admin/{id}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    @Operation(summary = "Admin only", description = "Diplom va statuslar bilan to'liq ma'lumot")
    public ResponseEntity<DoctorFullDTO> getForAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(doctorDetailsService.getForAdmin(id));
    }

    // 4. Barcha uchun (Public) ko'rish
    @GetMapping("/{id}")
    @Operation(summary = "Public", description = "Shifokor profilini ko'rish")
    public ResponseEntity<DoctorPublicDTO> getForPublic(@PathVariable Long id) {
        return ResponseEntity.ok(doctorDetailsService.getForPublic(id));
    }
}
