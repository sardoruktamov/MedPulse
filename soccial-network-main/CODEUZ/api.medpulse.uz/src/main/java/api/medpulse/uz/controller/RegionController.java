package api.medpulse.uz.controller;

import api.medpulse.uz.entity.DistrictEntity;
import api.medpulse.uz.entity.RegionEntity;
import api.medpulse.uz.repository.DistrictRepository;
import api.medpulse.uz.repository.RegionRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/region")
@RequiredArgsConstructor
@Tag(name = "Region & District", description = "Viloyat va Tumanlar ro'yxati")
public class RegionController {

    private final RegionRepository regionRepository;
    private final DistrictRepository districtRepository;

    // 1. Barcha viloyatlarni olish
    @GetMapping("/list")
    public ResponseEntity<List<RegionEntity>> getRegionList() {
        return ResponseEntity.ok(regionRepository.findAllByOrderByNameAsc());
    }

    // 2. Viloyat ID si bo'yicha tumanlarni olish
    // Frontend: Toshkent viloyatini tanlasa (ID=11), /api/v1/region/districts/11 ga so'rov yuboradi
    @GetMapping("/districts/{regionId}")
    public ResponseEntity<List<DistrictEntity>> getDistrictsByRegion(@PathVariable Integer regionId) {
        return ResponseEntity.ok(districtRepository.findByRegionIdOrderByNameAsc(regionId));
    }
}
