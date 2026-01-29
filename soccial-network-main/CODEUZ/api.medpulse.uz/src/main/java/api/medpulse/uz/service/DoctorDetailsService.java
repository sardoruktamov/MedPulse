package api.medpulse.uz.service;

import api.medpulse.uz.dto.doctor.DoctorApplyDTO;
import api.medpulse.uz.dto.doctor.DoctorFullDTO; // Buni o'zingiz yaratasiz (fieldlarni Mapping qilish uchun)
import api.medpulse.uz.dto.doctor.DoctorPublicDTO;
import api.medpulse.uz.entity.DoctorDetailsEntity;
import api.medpulse.uz.entity.ProfileEntity;
import api.medpulse.uz.entity.ProfileRoleEntity;
import api.medpulse.uz.enums.ApplicationStatus;
import api.medpulse.uz.enums.ProfileRole;
import api.medpulse.uz.exps.AppBadException;
import api.medpulse.uz.repository.DoctorDetailsRepository;
import api.medpulse.uz.repository.ProfileRepository;
import api.medpulse.uz.repository.ProfileRoleRepository;
import api.medpulse.uz.service.AttachService;
import api.medpulse.uz.util.SpringSecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DoctorDetailsService {

    @Autowired
    private DoctorDetailsRepository doctorDetailsRepository;
    @Autowired
    private final ProfileRepository profileRepository;

    @Autowired
    private ProfileRoleRepository profileRoleRepository;
    @Autowired
    private AttachService attachService;

    /**
     * 1. ARIZA TOPSHIRISH (User application to become a doctor)
     */
    public String apply(DoctorApplyDTO dto) {
        Integer currentUserId = SpringSecurityUtil.getCurrentUserId();

        // 1. Profilni tekshiramiz
        ProfileEntity profile = profileRepository.findById(currentUserId)
                .orElseThrow(() -> new AppBadException("Profil topilmadi"));

        // 2. Bu odam oldin ariza topshirganmi?
        Optional<DoctorDetailsEntity> optional = doctorDetailsRepository.findByProfileId(currentUserId);

        DoctorDetailsEntity entity;

        if (optional.isPresent()) {
            // --- ESKI ARIZANI TAHRIRLASH ---
            entity = optional.get();

            // Agar allaqachon APPROVED bo'lsa, qayta topshirolmaydi
            if (entity.getStatus().equals(ApplicationStatus.APPROVED)) {
                throw new AppBadException("Siz allaqachon Doctorsiz!");
            }
            // Agar PENDING bo'lsa ham kutishi kerak (yoki tahrirlashga ruxsat berish mumkin)

            // Statusni qayta PENDING qilamiz (Admin qayta ko'rishi uchun)
            entity.setStatus(ApplicationStatus.PENDING);
            entity.setUpdatedDate(LocalDateTime.now());

        } else {
            // --- YANGI ARIZA ---
            entity = new DoctorDetailsEntity();
            entity.setProfile(profile);
            entity.setCreatedDate(LocalDateTime.now());
        }

        // 3. Ma'lumotlarni to'ldirish (Mapping)
        entity.setSpeciality(dto.getSpeciality());
        entity.setUniversityName(dto.getUniversityName());
        entity.setDegree(dto.getDegree());
        entity.setGraduatedDate(dto.getGraduatedDate());
        entity.setExperienceYear(dto.getExperienceYear());
        entity.setCurrentWorkplace(dto.getCurrentWorkplace());
        entity.setAgreementPolicy(dto.getAgreementPolicy());

        // Diplom rasmi
        entity.setDiplomId(dto.getDiplomId());
        // Eslatma: RejectionReason ni o'chirmaymiz (Tarix uchun qoladi)

        doctorDetailsRepository.save(entity);
        return "Ariza yuborildi. Tez orada ko'rib chiqiladi.";
    }

    /**
     * 2. STATUS O'ZGARTIRISH (Admin)
     * Bu metodda ROLE o'zgaradi!
     */
    public String changeStatus(Long doctorDetailsId, ApplicationStatus newStatus, String reason) {
        // 1. Arizani topamiz
        DoctorDetailsEntity entity = doctorDetailsRepository.findById(doctorDetailsId)
                .orElseThrow(() -> new AppBadException("Ariza topilmadi"));

        if (newStatus.equals(ApplicationStatus.REJECTED)) {
            // --- RAD ETISH ---
            if (reason == null || reason.trim().isEmpty()) {
                throw new AppBadException("Rad etish sababi yozilishi shart!");
            }
            entity.setStatus(ApplicationStatus.REJECTED);
            entity.setRejectionReason(reason);
        }
        else if (newStatus.equals(ApplicationStatus.APPROVED)) {
            // --- TASDIQLASH ---
            entity.setStatus(ApplicationStatus.APPROVED);
            entity.setRejectionReason(null);

            // 🔥 ROLNI QO'SHISH 🔥

            // 1. Profil ID sini olamiz
            Integer profileId = entity.getProfile().getId();

            // 2. Hozirgi rollarni tekshiramiz (Repositorydagi tayyor Query orqali)
            List<ProfileRole> currentRoles = profileRoleRepository.getAllRolesListByProfileId(profileId);

            boolean isAlreadyDoctor = currentRoles.contains(ProfileRole.ROLE_DOCTOR);

            if (!isAlreadyDoctor) {
                ProfileRoleEntity newRoleEntity = new ProfileRoleEntity();

                // DIQQAT: Sizda 'profile' maydoni insertable=false bo'lgani uchun,
                // biz to'g'ridan-to'g'ri ID ustuniga set qilamiz.
                newRoleEntity.setProfileId(profileId);

                newRoleEntity.setRoles(ProfileRole.ROLE_DOCTOR);
                newRoleEntity.setCreatedDate(LocalDateTime.now());

                profileRoleRepository.save(newRoleEntity);
            }
        }

        // DoctorDetails ni saqlash
        doctorDetailsRepository.save(entity);
        return "Status o'zgartirildi: " + newStatus;
    }

    /**
     * 3. GET ONE (ADMIN UCHUN) - TO'LIQ MA'LUMOT
     * Ichida Diplom rasmi va rad etish sabablari bor.
     */
    public DoctorFullDTO getForAdmin(Long id) {
        DoctorDetailsEntity entity = getEntity(id); // Pastdagi yordamchi metod

        DoctorFullDTO dto = new DoctorFullDTO();
        dto.setId(entity.getId());
        dto.setProfileId(entity.getProfile().getId());
        dto.setFullName(entity.getProfile().getName()); // Ism Profile dan olinadi
        //  Admin uchun Avatar 🔥
        if (entity.getProfile().getPhotoId() != null) {
            dto.setAvatar(attachService.attachDTO(entity.getProfile().getPhotoId()));
        }
        dto.setSpeciality(entity.getSpeciality());
        dto.setUniversityName(entity.getUniversityName());
        dto.setDegree(entity.getDegree());
        dto.setCurrentWorkplace(entity.getCurrentWorkplace());
        dto.setStatus(entity.getStatus());
        dto.setRejectionReason(entity.getRejectionReason());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setUpdatedDate(entity.getUpdatedDate());

        // Diplom ID sini berib, to'liq URL olamiz
        if (entity.getDiplomId() != null) {
            dto.setDiplom(attachService.attachDTO(entity.getDiplomId()));
        }

        return dto;
    }

    /**
     * 4. GET ONE (PUBLIC/USER UCHUN) - QISQA MA'LUMOT
     * Faqat tasdiqlangan (APPROVED) doktorlar uchun ishlatilishi kerak.
     */
    public DoctorPublicDTO getForPublic(Long id) {
        DoctorDetailsEntity entity = getEntity(id);

        // Agar Doctor tasdiqlanmagan bo'lsa, oddiy odamlar ko'ra olmasligi kerak
        if (!entity.getStatus().equals(ApplicationStatus.APPROVED)) {
            // Yoki exception otamiz, yoki ruxsat bermaymiz (Biznes talabga qarab)
            throw new AppBadException("Bu shifokor bo'lmasligi mumkin");
        }

        DoctorPublicDTO dto = new DoctorPublicDTO();
        dto.setId(entity.getId());
        dto.setProfileId(entity.getProfile().getId());
        dto.setFullName(entity.getProfile().getName());

        // Profil rasmini (Avatar) ham qo'shib ketamiz
        if (entity.getProfile().getPhotoId() != null) {
            dto.setAvatar(attachService.attachDTO(entity.getProfile().getPhotoId()));
        }

        dto.setSpeciality(entity.getSpeciality());
        dto.setUniversityName(entity.getUniversityName());
        dto.setDegree(entity.getDegree());
        dto.setCurrentWorkplace(entity.getCurrentWorkplace());
        dto.setExperienceYear(entity.getExperienceYear());

        // DIPLOM BERILMAYDI!
        return dto;
    }

    /**
     * Yordamchi metod (Entity topish)
     */
    public DoctorDetailsEntity getEntity(Long id) {
        return doctorDetailsRepository.findById(id)
                .orElseThrow(() -> new AppBadException("Doctor details topilmadi"));
    }
}
