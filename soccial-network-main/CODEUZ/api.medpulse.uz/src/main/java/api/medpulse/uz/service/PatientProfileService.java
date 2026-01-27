package api.medpulse.uz.service;

import api.medpulse.uz.dto.patient.PatientCreateDTO;
import api.medpulse.uz.dto.patient.PatientUpdateDTO;
import api.medpulse.uz.entity.AttachEntity;
import api.medpulse.uz.entity.PatientProfileEntity;
import api.medpulse.uz.entity.ProfileEntity;
import api.medpulse.uz.exps.AppBadException;
import api.medpulse.uz.repository.PatientProfileRepository;
import api.medpulse.uz.repository.ProfileRepository;
import api.medpulse.uz.util.SpringSecurityUtil;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.security.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientProfileService {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private PatientProfileRepository patientProfileRepository;
    @Autowired
    private AttachService attachService;

    // Profilni yangilash
    public PatientProfileEntity update(String profileId, PatientUpdateDTO dto) {
        // 1. Hozirgi kirgan foydalanuvchi (Ota) ID sini olamiz
        Integer currentUserId = SpringSecurityUtil.getCurrentUserId();

        // 2. Profilni qidiramiz: ID si bo'yicha VA egasi shu odam ekanligi bo'yicha
        PatientProfileEntity entity = patientProfileRepository.findByIdAndOwner_Id(profileId, currentUserId)
                .orElseThrow(() -> new AppBadException("Profile not found or access denied/Profil topilmadi yoki kirish taqiqlandi"));

        String deletePhotoId = null;

        // Agar yangi rasm kelgan bo'lsa VA u eski rasmdan farq qilsa
        if (dto.getPhotoId() != null && !dto.getPhotoId().equals(entity.getPhotoId())) {
            // Eski rasmni ID sini eslab qolamiz
            deletePhotoId = entity.getPhotoId();
            // Yangi rasm ID sini o'rnatamiz
            entity.setPhotoId(dto.getPhotoId());
            AttachEntity newPhoto = attachService.getEntity(dto.getPhotoId());
            entity.setPhoto(newPhoto);
        }

        // 3. Ma'lumotlarni yangilaymiz (faqat null bo'lmaganlarini)
        if (dto.getFullName() != null) entity.setFullName(dto.getFullName());
        if (dto.getBirthDate() != null) entity.setBirthDate(dto.getBirthDate());
        if (dto.getGender() != null) entity.setGender(dto.getGender());

        // Tibbiy qism
        if (dto.getBloodGroup() != null) entity.setBloodGroup(dto.getBloodGroup()); // Enum bo'lsa .name() shart emas
        if (dto.getWeight() != null) entity.setWeight(dto.getWeight());
        if (dto.getHeight() != null) entity.setHeight(dto.getHeight());
        if (dto.getWorkingBloodPressure() != null) entity.setWorkingBloodPressure(dto.getWorkingBloodPressure());

        // 4. Saqlash
        patientProfileRepository.save(entity);
        // 2. Agar rasm o'zgargan bo'lsa, eski rasmni AttachService orqali o'chiramiz
        if (deletePhotoId != null) {
            attachService.delete(deletePhotoId);
        }

        return entity;
    }

    public PatientProfileEntity create(PatientCreateDTO dto) {
        // 1. Joriy foydalanuvchi (Ota) ID sini olamiz
        Integer currentUserId = SpringSecurityUtil.getCurrentUserId();

        // 2. Ota (Owner) entitysini bazadan olamiz
        ProfileEntity owner = profileRepository.findById(currentUserId)
                .orElseThrow(() -> new AppBadException("Owner profile not found/Profil egasi(Ota) topilmadi"));

        // 3. Yangi Bemor profilini yaratamiz
        PatientProfileEntity entity = new PatientProfileEntity();
        entity.setFullName(dto.getFullName());
        entity.setBirthDate(dto.getBirthDate());
        entity.setGender(dto.getGender());
        entity.setOwner(owner); // <--- BOG'LASH JARAYONI

        // 4. Qo'shimcha ma'lumotlar bor bo'lsa, ularni ham qo'shamiz
        if (dto.getPhotoId() != null) entity.setPhotoId(dto.getPhotoId());
        if (dto.getBloodGroup() != null) entity.setBloodGroup(dto.getBloodGroup());
        if (dto.getWeight() != null) entity.setWeight(dto.getWeight());
        if (dto.getHeight() != null) entity.setHeight(dto.getHeight());

        // 5. Saqlash
        return patientProfileRepository.save(entity);
    }

    // Foydalanuvchining barcha profillarini olish (O'ziniki va oilasiniki)
    // Bu metod frontendga qaysi ID ni update qilish kerakligini bilish uchun kerak
    public List<PatientProfileEntity> getMyFamilyProfiles() {
        Integer currentUserId = SpringSecurityUtil.getCurrentUserId();
        return patientProfileRepository.findByOwner_Id(currentUserId);
    }
}
