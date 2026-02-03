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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class QrCodeService {

    @Autowired
    private PatientProfileRepository patientProfileRepository;

    @Value("${baze.url.address}")
    private String baseUrl;

    @Value("${medpulse.qr.width}")
    private int width;

    @Value("${medpulse.qr.height}")
    private int height;

    public byte[] generateQrForPatient(String patientId) {
        // 1. Bazadan bemor ma'lumotlarini olamiz
        PatientProfileEntity patient = patientProfileRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Bemor topilmadi"));

        // 2. Ma'lumotlarni tekshiramiz (Null bo'lsa bo'sh joy qoldiramiz)
            // DIQQAT: Agar eski bemorlarda token bo'lmasa, shu joyda generatsiya qilib saqlab qo'yamiz
        if (patient.getQrToken() == null) {
            patient.setQrToken(RandomUtil.generateQrToken());
            patientProfileRepository.save(patient);
        }
        String bloodGroup = (patient.getBloodGroup() != null) ? String.valueOf(patient.getBloodGroup()) : "Aniqlanmagan";
        String sosPhone = (patient.getEmergencyContactPhone() != null) ? patient.getEmergencyContactPhone() : "Yo'q";

        // 3. QR kod tarkibini shakllantiramiz (Siz so'ragan format)
        // Format: URL + yangi qator + Qon guruhi + yangi qator + SOS
        String shortUrl = String.format("%s/api/v1/patient/q/%s", baseUrl, patient.getQrToken());
        String qrContent = String.format("%s\n\nQon: %s\nSOS: %s",
                shortUrl,
                bloodGroup,
                sosPhone);
        // TODO QR KODDAGI TELEFON NOMERGA YAQIN QARINDOSHINI NOMERINI YOZISH
        // 4. QR kod (Rasm) generatsiya jarayoni
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, width, height);

            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();

            // Ranglar: Qora (0xFF000000) va Oq (0xFFFFFFFF)
            MatrixToImageConfig config = new MatrixToImageConfig(0xFF000000, 0xFFFFFFFF);

            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream, config);

            return pngOutputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("QR kod yaratishda xatolik yuz berdi", e);
        }
    }
}
