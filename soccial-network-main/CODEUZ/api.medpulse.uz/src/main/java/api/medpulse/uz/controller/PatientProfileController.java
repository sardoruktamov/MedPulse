package api.medpulse.uz.controller;

import api.medpulse.uz.dto.patient.PatientCreateDTO;
import api.medpulse.uz.dto.patient.PatientProfileDTO;
import api.medpulse.uz.dto.patient.PatientUpdateDTO;
import api.medpulse.uz.entity.PatientProfileEntity;
import api.medpulse.uz.repository.PatientProfileRepository;
import api.medpulse.uz.service.PatientProfileService;
import api.medpulse.uz.service.QrCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import api.medpulse.uz.util.SwaggerExamples;
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
        @Operation(summary = "Mening profillarimni olish", description = "Tizimga kirgan foydalanuvchining o'z profili va oila a'zolarining profillarini ro'yxatini qaytaradi.")
        @ApiResponse(responseCode = "200", description = "Profillar muvaffaqiyatli olindi!", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
                        @ExampleObject(value = SwaggerExamples.PATIENT_PROFILES_LIST_SUCCESS) }))
        public ResponseEntity<List<PatientProfileDTO>> getMyProfiles() {
                return ResponseEntity.ok(patientProfileService.getMyFamilyProfiles());
        }

        @GetMapping("/{id}")
        @Operation(summary = "Bemor profilini olish", description = "Berilgan ID orqali bitta bemor profilini olish.")
        @ApiResponse(responseCode = "200", description = "Bemor profili muvaffaqiyatli topildi!", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
                        @ExampleObject(value = SwaggerExamples.PATIENT_PROFILE_RESPONSE_SUCCESS) }))
        @ApiResponse(responseCode = "404", description = "Bemor topilmadi!", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
                        @ExampleObject(value = SwaggerExamples.PATIENT_NOT_FOUND_ERROR_EXAMPLE) }))
        public ResponseEntity<PatientProfileDTO> getById(
                        @Parameter(description = "Bemor identifikatori (UUID formatida)") @PathVariable("id") String id) {
                return ResponseEntity.ok(patientProfileService.getById(id));
        }

        // 2. Profilni to'ldirish (Update)
        @PutMapping("/{id}")
        @Operation(summary = "Bemor profilini yangilash", description = "Berilgan ID orqali bemor profilidagi asosiy ma'lumotlarni yangilash.")
        @ApiResponse(responseCode = "200", description = "Profil muvaffaqiyatli yangilandi!", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
                        @ExampleObject(value = SwaggerExamples.PATIENT_PROFILE_RESPONSE_SUCCESS) }))
        @ApiResponse(responseCode = "400", description = "Ma'lumotni saqlashda xatolik!", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
                        @ExampleObject(value = SwaggerExamples.PATIENT_PROFILE_ERROR_EXAMPLE) }))
        public ResponseEntity<PatientProfileDTO> updateProfile(
                        @Parameter(description = "Yangilanayotgan bemorning identifikatori (UUID)") @PathVariable("id") String id,
                        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Yangilanayotgan ma'lumotlar jismoni", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
                                        @ExampleObject(value = SwaggerExamples.PATIENT_UPDATE_REQUEST_EXAMPLE) })) @RequestBody PatientUpdateDTO dto) {
                return ResponseEntity.ok(patientProfileService.update(id, dto));
        }

        // 3. Yangi oila a'zosini qo'shish
        @PostMapping("/family")
        @Operation(summary = "Oila a'zosini qo'shish", description = "O'z profiliga yangi oila a'zosi profilini qo'shish yaratish.")
        @ApiResponse(responseCode = "200", description = "Oila a'zosi muvaffaqiyatli qo'shildi!", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
                        @ExampleObject(value = SwaggerExamples.PATIENT_PROFILE_RESPONSE_SUCCESS) }))
        @ApiResponse(responseCode = "400", description = "Ma'lumotni kiritishda xatolik!", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
                        @ExampleObject(value = SwaggerExamples.PATIENT_PROFILE_ERROR_EXAMPLE) }))
        public ResponseEntity<PatientProfileDTO> createMember(
                        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Yangi oila a'zosi ma'lumotlari jismoni", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
                                        @ExampleObject(value = SwaggerExamples.PATIENT_CREATE_REQUEST_EXAMPLE) })) @Valid @RequestBody PatientCreateDTO dto) {
                return ResponseEntity.ok(patientProfileService.create(dto));
        }

        // Bemor o'z ilovasida QR kod rasmini ko'rish uchun
        @GetMapping(value = "/qr/{patientId}", produces = MediaType.IMAGE_PNG_VALUE)
        @Operation(summary = "QR kodni ko'rish", description = "Bemor o'z ilovasida shifokorlarga ko'rsatish uchun mo'ljallangan QR kod rasmini PNG formatida qaytaradi.")
        @ApiResponse(responseCode = "200", description = "QR kod muvaffaqiyatli generatsiya qilindi va qaytarildi")
        @ApiResponse(responseCode = "404", description = "Bemor topilmadi!")
        public ResponseEntity<byte[]> getQrImage(
                        @Parameter(description = "Bemorning identifikatori (UUID)") @PathVariable String patientId) {
                return ResponseEntity.ok(qrCodeService.generateQrForPatient(patientId));
        }

}
