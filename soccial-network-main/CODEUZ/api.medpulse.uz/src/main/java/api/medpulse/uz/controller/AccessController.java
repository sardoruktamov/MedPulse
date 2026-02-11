package api.medpulse.uz.controller;

import api.medpulse.uz.service.AccessControlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/access")
@RequiredArgsConstructor
public class AccessController {

    private final AccessControlService accessService;

    // 1. Ota kod oladi (USER roli kerak)
    @PostMapping("/generate/{patientId}")
    public ResponseEntity<String> generateCode(@PathVariable String patientId) {
        return ResponseEntity.ok(accessService.generateAccessCode(patientId));
    }

    // 2. Doktor kodni kiritadi (DOCTOR roli kerak)
    @PostMapping("/doctor-verify")
    public ResponseEntity<String> verifyCode(@RequestParam("code") String code) {
        // Natija: PatientID qaytadi. Frontend shuni olib, /patient-history/{id} ga yo'naltiradi
        return ResponseEntity.ok(accessService.verifyCodeAndGrantAccess(code));
    }
}