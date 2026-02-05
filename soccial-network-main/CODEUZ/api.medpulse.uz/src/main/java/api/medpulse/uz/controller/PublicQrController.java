package api.medpulse.uz.controller;

import api.medpulse.uz.dto.qr.QrInfoResponseDTO;
import api.medpulse.uz.service.QrScanService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public/qr")
@RequiredArgsConstructor
@Tag(name = "Public QR", description = "Shifokorlar va Frontend uchun ochiq API")
public class PublicQrController {

    private final QrScanService qrScanService;

    // Frontend shu yerga murojaat qilib JSON oladi
    // GET /api/v1/public/qr/info/{qrToken}
    @GetMapping("/info/{qrToken}")
    public ResponseEntity<QrInfoResponseDTO> getPatientInfo(@PathVariable String qrToken) {
        return ResponseEntity.ok(qrScanService.getPatientDataByToken(qrToken));
    }
}
