package api.medpulse.uz.controller;

import api.medpulse.uz.dto.patient.PatientCreateDTO;
import api.medpulse.uz.dto.patient.PatientProfileDTO;
import api.medpulse.uz.dto.patient.PatientUpdateDTO;
import api.medpulse.uz.entity.PatientProfileEntity;
import api.medpulse.uz.repository.PatientProfileRepository;
import api.medpulse.uz.service.PatientProfileService;
import api.medpulse.uz.service.QrCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/patient")
@RequiredArgsConstructor
@Tag(name = "PatientProfile", description = "Bemor profili va Oila a'zolari bilan ishlash")
public class PatientProfileController {


    private final QrCodeService qrCodeService;
    private final PatientProfileRepository patientProfileRepository; // Token orqali topish uchun

    private final PatientProfileService patientProfileService;

    // 1. Mening profillarim (O'zim va oilam) ro'yxati
    // Frontend shu yerdan ID larni oladi
    @GetMapping("/my-profiles")
    public ResponseEntity<List<PatientProfileDTO>> getMyProfiles() {
        return ResponseEntity.ok(patientProfileService.getMyFamilyProfiles());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get One", description = "Bemor profilini ID orqali olish")
    public ResponseEntity<PatientProfileDTO> getById(@PathVariable String id) {
        return ResponseEntity.ok(patientProfileService.getById(id));
    }

    // 2. Profilni to'ldirish (Update)
    @PutMapping("/{id}")
    public ResponseEntity<PatientProfileDTO> updateProfile(
            @PathVariable("id") String id, // UUID
            @RequestBody PatientUpdateDTO dto) {
        return ResponseEntity.ok(patientProfileService.update(id, dto));
    }

    // 3. Yangi oila a'zosini qo'shish
    @PostMapping("/family")
    public ResponseEntity<PatientProfileDTO> createMember(@Valid @RequestBody PatientCreateDTO dto) {
        return ResponseEntity.ok(patientProfileService.create(dto));
    }


    // Bemor o'z ilovasida QR kod rasmini ko'rish uchun
    @GetMapping(value = "/qr/{patientId}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getQrImage(@PathVariable String patientId) {
        return ResponseEntity.ok(qrCodeService.generateQrForPatient(patientId));
    }

}
