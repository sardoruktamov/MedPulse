package api.medpulse.uz.service;

import api.medpulse.uz.dto.qr.CriticalRecordDTO;
import api.medpulse.uz.dto.qr.QrInfoResponseDTO;
import api.medpulse.uz.entity.HealthRecordEntity;
import api.medpulse.uz.entity.PatientProfileEntity;
import api.medpulse.uz.repository.HealthRecordRepository;
import api.medpulse.uz.repository.PatientProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class QrScanService {

    private final PatientProfileRepository patientProfileRepository;
    private final HealthRecordRepository healthRecordRepository;

    public QrInfoResponseDTO getPatientDataByToken(String qrToken) {
        // 1. Token orqali bemorni topish
        PatientProfileEntity patient = patientProfileRepository.findByQrToken(qrToken)
                .orElseThrow(() -> new RuntimeException("QR kod eskirgan yoki noto'g'ri"));

        // 2. Kritik kasalliklarni olish
        List<HealthRecordEntity> records = healthRecordRepository
                .findAllByPatientIdAndIsCriticalTrue(patient.getId());

        // 3. Kritik kasalliklarni DTO ga o'girish
        List<CriticalRecordDTO> recordDTOS = records.stream()
                .map(r -> CriticalRecordDTO.builder()
                        .diseaseName(r.getDiseaseName())
                        // Kritik bo'lgani uchun davolanish chorasini ko'rsatgan ma'qul
                        .treatment(r.getTreatment())
                        .doctorName(r.getDoctorName()) // Qo'shimcha ma'lumot
                        .hospitalName(r.getHospitalName()) // Qo'shimcha ma'lumot

                        // XATOLIK DUZATILDI: getCreatedDate() -> getRecordDate()
                        .date(r.getRecordDate() != null ? r.getRecordDate().toString() : "")
                        .build())
                .toList();

        // 4. Asosiy DTO ni qaytarish
        return QrInfoResponseDTO.builder()
                .fullName(patient.getFullName())
                // TODO: PhotoService orqali rasm URLini olish kerak (masalan: /api/v1/attach/open/{id})
                .photoUrl(patient.getPhotoId() != null ? "/api/v1/attach/open/" + patient.getPhotoId() : null)
                .birthDate(patient.getBirthDate() != null ? patient.getBirthDate().toString() : null)
                .bloodGroup(patient.getBloodGroup() != null ? patient.getBloodGroup().name() : "Aniqlanmagan")
                .weight(patient.getWeight())
                .height(patient.getHeight())
                .regionId(patient.getRegion().getId())
                .districtId(patient.getDistrict().getId())
                .address(patient.getAddress())
                .workingBloodPressure(patient.getWorkingBloodPressure())
                .allergies(patient.getAllergies())
                .emergencyContactName(patient.getEmergencyContactName())
                .emergencyContactPhone(patient.getEmergencyContactPhone())
                .criticalRecords(recordDTOS)
                .build();
    }
}
