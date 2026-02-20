package api.medpulse.uz.controller;

import api.medpulse.uz.entity.UniversityEntity;
import api.medpulse.uz.service.UniversityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/university")
@RequiredArgsConstructor
@Tag(name = "University (Dictionary)", description = "OTMlar ro'yxati")
public class UniversityController {

    private final UniversityService service;

    @GetMapping("/list")
    @Operation(summary = "Public", description = "Ro'yxatdan o'tish uchun barcha OTMlar")
    public ResponseEntity<List<UniversityEntity>> getList() {
        return ResponseEntity.ok(service.getList());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    @Operation(summary = "Admin", description = "Yangi OTM qo'shish")
    public ResponseEntity<UniversityEntity> create(@RequestParam String name) {
        return ResponseEntity.ok(service.create(name));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<UniversityEntity> update(@PathVariable Integer id, @RequestParam String name) {
        return ResponseEntity.ok(service.update(id, name));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<String> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.ok("O'chirildi (Arxivlandi)");
    }
}