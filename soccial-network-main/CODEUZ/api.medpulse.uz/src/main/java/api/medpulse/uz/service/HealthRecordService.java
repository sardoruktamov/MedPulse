package api.medpulse.uz.service;

import api.medpulse.uz.dto.HealthRecord.HealthRecordCreateDTO;
import api.medpulse.uz.entity.HealthRecordEntity;
import api.medpulse.uz.entity.PatientProfileEntity;
import api.medpulse.uz.exps.AppBadException;
import api.medpulse.uz.repository.HealthRecordRepository;
import api.medpulse.uz.repository.PatientProfileRepository;
import api.medpulse.uz.util.SpringSecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class HealthRecordService {

    @Autowired
    private HealthRecordRepository healthRecordRepository;
    @Autowired
    private PatientProfileRepository patientProfileRepository;

    public HealthRecordEntity create(HealthRecordCreateDTO dto) {
        // 1. Kim yozyapti? (Ota)
        Integer currentUserId = SpringSecurityUtil.getCurrentUserId();

        // 2. Kim uchun yozyapti? (Patient)
        // Tekshiramiz: patientId shu Otaga tegishlimi?
        PatientProfileEntity patient = patientProfileRepository.findByIdAndOwner_Id(dto.getPatientId(), currentUserId)
                .orElseThrow(() -> new AppBadException("Bemor topilmadi yoki huquqingiz yo'q"));

        // 3. Ma'lumotlarni to'ldirish
        HealthRecordEntity entity = new HealthRecordEntity();
        entity.setPatient(patient);

        entity.setDiseaseName(dto.getDiseaseName());
        entity.setRecordDate(dto.getRecordDate()); // Kasal bo'lgan kuni

        entity.setDoctorName(dto.getDoctorName());
        entity.setHospitalName(dto.getHospitalName());
        entity.setTreatment(dto.getTreatment());
        entity.setNote(dto.getNote());

        entity.setIsCritical(dto.getIsCritical());

        if (dto.getPhotoId() != null) entity.setPhotoId(dto.getPhotoId());

        return healthRecordRepository.save(entity);
    }

    // Ro'yxatni olish
    public List<HealthRecordEntity> getMedicalHistory(String patientId) {
        Integer currentUserId = SpringSecurityUtil.getCurrentUserId();
        // Xavfsizlik: Faqat o'z oilasini ko'ra olsin
        boolean isOwner = patientProfileRepository.findByIdAndOwner_Id(patientId, currentUserId).isPresent();
        if (!isOwner) throw new AppBadException("Ruxsat yo'q");

        return healthRecordRepository.findByPatientIdOrderByRecordDateDesc(patientId);
    }
}
