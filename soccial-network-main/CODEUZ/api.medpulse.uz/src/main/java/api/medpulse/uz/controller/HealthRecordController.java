package api.medpulse.uz.controller;

import api.medpulse.uz.dto.AppResponse;
import api.medpulse.uz.dto.HealthRecord.HealthRecordCreateDTO;
import api.medpulse.uz.dto.HealthRecord.HealthRecordDTO;
import api.medpulse.uz.dto.HealthRecord.HealthRecordUpdateDTO;
import api.medpulse.uz.entity.HealthRecordEntity;
import api.medpulse.uz.service.HealthRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/health-record")
@RequiredArgsConstructor
@Tag(name = "HealthRecord", description = "Kasallik tarixi bilan ishlash")
public class HealthRecordController {

    @Autowired
    private HealthRecordService healthRecordService;

    @PostMapping("/create")
    public ResponseEntity<HealthRecordDTO> create(@Valid @RequestBody HealthRecordCreateDTO dto) {
        return ResponseEntity.ok(healthRecordService.create(dto));
    }

    @GetMapping("/list/{patientId}")
    public ResponseEntity<List<HealthRecordDTO>> getList(@PathVariable String patientId) {
        return ResponseEntity.ok(healthRecordService.getMedicalHistory(patientId));
    }

    // ... create va list metodlari ...

    @PutMapping("/{id}")
    @Operation(summary = "Update record", description = "Kasallik tarixini tahrirlash")
    public ResponseEntity<HealthRecordDTO> update(
            @PathVariable Long id,
            @RequestBody HealthRecordUpdateDTO dto) {
        return ResponseEntity.ok(healthRecordService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete record", description = "Kasallik tarixini o'chirish")
    public ResponseEntity<AppResponse<String>> delete(@PathVariable Long id) {
        return ResponseEntity.ok(healthRecordService.delete(id));
    }
}
