package api.medpulse.uz.controller;

import api.medpulse.uz.service.AccessControlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/access")
@RequiredArgsConstructor
@Tag(name = "Access Controller", description = "Bemorlar uchun kirish kodlarini yaratish va tekshirish API-lari")
public class AccessController {

    private final AccessControlService accessService;

    // 1. Ota kod oladi (USER roli kerak)
    @PostMapping("/generate/{patientId}")
    @Operation(summary = "Kirish kodini yaratish", description = "Bemor uchun vaqtinchalik kirish kodini yaratadi. USER roli talab qilinadi.")
    public ResponseEntity<String> generateCode(
            @Parameter(description = "Bemorning identifikatori (ID)") @PathVariable String patientId) {
        return ResponseEntity.ok(accessService.generateAccessCode(patientId));
    }

    // 2. Doktor kodni kiritadi (DOCTOR roli kerak)
    @PostMapping("/doctor-verify")
    @Operation(summary = "Kirish kodini tekshirish", description = "Shifokor tomonidan kiritilgan kirish kodini tekshiradi. DOCTOR roli talab qilinadi. Bemor ID-sini qaytaradi.")
    public ResponseEntity<String> verifyCode(
            @Parameter(description = "Tekshirilishi kerak bo'lgan kirish kodi") @RequestParam("code") String code) {
        // Natija: PatientID qaytadi. Frontend shuni olib, /patient-history/{id} ga
        // yo'naltiradi
        return ResponseEntity.ok(accessService.verifyCodeAndGrantAccess(code));
    }
}