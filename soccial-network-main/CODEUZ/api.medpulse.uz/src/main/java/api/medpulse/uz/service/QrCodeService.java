package api.medpulse.uz.service;

import api.medpulse.uz.entity.PatientProfileEntity;
import api.medpulse.uz.repository.PatientProfileRepository;
import api.medpulse.uz.util.RandomUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor
public class QrCodeService {

    private final PatientProfileRepository patientProfileRepository;

    @Value("${app.frontend.domain}")
    private String baseUrl; // Masalan: https://medpulse.uz

    @Value("${medpulse.qr.width}")
    private int width;

    @Value("${medpulse.qr.height}")
    private int height;

    public byte[] generateQrForPatient(String patientId) {
        PatientProfileEntity patient = patientProfileRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Bemor topilmadi"));

        // Token tekshirish va yaratish
        if (patient.getQrToken() == null) {
            patient.setQrToken(RandomUtil.generateQrToken());
            patientProfileRepository.save(patient);
        }

        // URL MANZILI: Bu manzil Frontenddagi sahifaga olib borishi kerak!
        // Masalan: https://medpulse.uz/q/XyZ123
        String shortUrl = String.format("%s/q/%s", baseUrl, patient.getQrToken());

        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(shortUrl, BarcodeFormat.QR_CODE, width, height);
            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageConfig config = new MatrixToImageConfig(0xFF000000, 0xFFFFFFFF);
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream, config);
            return pngOutputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("QR generatsiya xatosi", e);
        }
    }
}