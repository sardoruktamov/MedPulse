package api.medpulse.uz.service;

import api.medpulse.uz.dto.AppResponse;
import api.medpulse.uz.dto.HealthRecord.HealthRecordCreateDTO;
import api.medpulse.uz.dto.HealthRecord.HealthRecordUpdateDTO;
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

    /**
     * 3. Tahrirlash (Update)
     */
    public HealthRecordEntity update(Long id, HealthRecordUpdateDTO dto) {
        // 1. Joriy foydalanuvchi (Ota)
        Integer currentUserId = SpringSecurityUtil.getCurrentUserId();

        // 2. Yozuvni topamiz
        HealthRecordEntity entity = healthRecordRepository.findById(id)
                .orElseThrow(() -> new AppBadException("Kasallik tarixi topilmadi"));

        // 3. XAVFSIZLIK: Bu yozuv egasi shu odammi?
        // entity -> getPatient() -> getOwner() -> getId()
        if (!entity.getPatient().getOwner().getId().equals(currentUserId)) {
            throw new AppBadException("Sizda bu yozuvni tahrirlash huquqi yo'q");
        }

        // 4. O'zgartirish (Faqat kelgan ma'lumotlarni)
        if (dto.getDiseaseName() != null) entity.setDiseaseName(dto.getDiseaseName());
        if (dto.getRecordDate() != null) entity.setRecordDate(dto.getRecordDate());
        if (dto.getDoctorName() != null) entity.setDoctorName(dto.getDoctorName());
        if (dto.getHospitalName() != null) entity.setHospitalName(dto.getHospitalName());
        if (dto.getTreatment() != null) entity.setTreatment(dto.getTreatment());
        if (dto.getNote() != null) entity.setNote(dto.getNote());
        if (dto.getIsCritical() != null) entity.setIsCritical(dto.getIsCritical());
        if (dto.getPhotoId() != null) entity.setPhotoId(dto.getPhotoId());

        return healthRecordRepository.save(entity);
    }

    /**
     * 4. O'chirish (Delete)
     */
    public AppResponse<String> delete(Long id) {
        // 1. Joriy foydalanuvchi
        Integer currentUserId = SpringSecurityUtil.getCurrentUserId();

        // 2. Yozuvni topamiz
        HealthRecordEntity entity = healthRecordRepository.findById(id)
                .orElseThrow(() -> new AppBadException("Kasallik tarixi topilmadi"));

        // 3. XAVFSIZLIK: Egasi ekanligini tekshirish
        if (!entity.getPatient().getOwner().getId().equals(currentUserId)) {
            throw new AppBadException("Sizda bu yozuvni o'chirish huquqi yo'q");
        }

        // 4. O'chiramiz
        healthRecordRepository.delete(entity);
        return new AppResponse<>("Kasallik tarixi muvoffaqiyatli o'chirildi.");
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
