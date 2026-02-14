package api.medpulse.uz.service;

import api.medpulse.uz.dto.AttachDTO;
import api.medpulse.uz.dto.doctor.DoctorApplyDTO;
import api.medpulse.uz.dto.doctor.DoctorFullDTO; // Buni o'zingiz yaratasiz (fieldlarni Mapping qilish uchun)
import api.medpulse.uz.dto.doctor.DoctorPublicDTO;
import api.medpulse.uz.entity.AttachEntity;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
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
            if (entity.getStatus().equals(ApplicationStatus.APPROVED)) {
                throw new AppBadException("Siz allaqachon Doctorsiz!");
            }
            entity.setStatus(ApplicationStatus.PENDING);
            entity.setUpdatedDate(LocalDateTime.now());

            // 🔥 1. DIPLOMLARNI YANGILASH VA TOZALASH 🔥
            updatePhotoList(entity.getDiplomList(), dto.getDiplomPhotoIds(), true); // true = Diplom (Entityga set qilish uchun pastda alohida ishlaymiz)

            // 🔥 2. SERTIFIKATLARNI YANGILASH VA TOZALASH 🔥
            updatePhotoList(entity.getCertificateList(), dto.getCertificatePhotoIds(), false);

        } else {
            // --- CREATE (YANGI) ---
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

        // DTO dagi ID larni -> Entity larga aylantirib, Asosiy Entityga berish kerak
        entity.setDiplomList(getAttachListFromIds(dto.getDiplomPhotoIds()));
        entity.setCertificateList(getAttachListFromIds(dto.getCertificatePhotoIds()));

        doctorDetailsRepository.save(entity);
        return "Ariza yuborildi. Tez orada ko'rib chiqiladi.";
    }

    /**
     * YORDAMCHI METOD: ID lar ro'yxatidan Entity lar ro'yxatini yasash
     */
    private List<AttachEntity> getAttachListFromIds(List<String> ids) {
        List<AttachEntity> list = new ArrayList<>();
        if (ids != null && !ids.isEmpty()) {
            for (String id : ids) {
                list.add(attachService.getEntity(id));
            }
        }
        return list;
    }

    /**
     * YORDAMCHI METOD: Keraksiz rasmlarni o'chirish (Diff Logic)
     * Bu metod faqat UPDATE bo'lganda ishlatiladi.
     */
    private void updatePhotoList(List<AttachEntity> oldEntities, List<String> newIds, boolean isMandatory) {
        if (newIds == null) return; // Agar null kelsa tegmaymiz (lekin DTOda majburiy bo'lsa validation o'tmaydi)

        // 1. Eskilarni ID sini olamiz
        List<String> oldIds = new ArrayList<>();
        if (oldEntities != null) {
            oldIds = oldEntities.stream().map(AttachEntity::getId).toList();
        }

        // 2. Keraksizlarni o'chiramiz (Eskida bor, Yangida yo'q)
        for (String oldId : oldIds) {
            if (!newIds.contains(oldId)) {
                try {
                    attachService.delete(oldId);
                } catch (Exception e) {
                    log.warn("Rasmni o'chirishda xato: {}", oldId);
                }
            }
        }
    }


    /**
     * 2. STATUS O'ZGARTIRISH (Admin)
     * Bu metodda ROLE o'zgaradi!
     */
    public String changeStatus(Long doctorDetailsId, ApplicationStatus newStatus, String reason) {
        // 1. Arizani topamiz (Faqat bir marta e'lon qilamiz)
        DoctorDetailsEntity entity = doctorDetailsRepository.findById(doctorDetailsId)
                .orElseThrow(() -> new AppBadException("Ariza topilmadi"));

        // 2. Statusni tekshiramiz
        if (entity.getStatus().equals(ApplicationStatus.APPROVED) && newStatus.equals(ApplicationStatus.APPROVED)) {
            return "Bu ariza allaqachon tasdiqlangan.";
        }

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
            entity.setRejectionReason(null); // Agar oldin rad etilgan bo'lsa, sababni o'chiramiz

            // 1. Profil ID sini olamiz
            Integer profileId = entity.getProfile().getId();

            // 2. Hozirgi rollarni tekshiramiz (Repositorydagi query orqali)
            List<ProfileRole> currentRoles = profileRoleRepository.getAllRolesListByProfileId(profileId);

            // Agar unda hali ROLE_DOCTOR yo'q bo'lsa, qo'shamiz
            if (!currentRoles.contains(ProfileRole.ROLE_DOCTOR)) {
                ProfileRoleEntity newRoleEntity = new ProfileRoleEntity();
                newRoleEntity.setProfileId(profileId);
                newRoleEntity.setRoles(ProfileRole.ROLE_DOCTOR);
                newRoleEntity.setCreatedDate(LocalDateTime.now());

                profileRoleRepository.save(newRoleEntity);
                log.info("SUPERADMIN tomonidan User NAME={} ga DOCTOR roli berildi, ID={}", entity.getProfile().getName(), profileId);
            }
        }else {
            // PENDING holatiga qaytarish (agar kerak bo'lsa)
            entity.setStatus(ApplicationStatus.PENDING);
        }

        // DoctorDetails ni saqlash (Update)
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
        // --- LISTLARNI DTO GA O'GIRISH ---
        dto.setDiplomList(toAttachDTOList(entity.getDiplomList()));
        dto.setCertificateList(toAttachDTOList(entity.getCertificateList()));

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
        // 🔥 SERTIFIKATLARNI ULASH 🔥
        // Bizda pastda "toAttachDTOList" yordamchi metodi bor edi (Admin qismida yozgandik)
        // O'shandan foydalanamiz:
        dto.setCertificateList(toAttachDTOList(entity.getCertificateList()));

        // DIPLOM BERILMAYDI!
        return dto;
    }

    // Yordamchi metod: List<Entity> -> List<DTO>
    private List<AttachDTO> toAttachDTOList(List<AttachEntity> entities) {
        List<AttachDTO> dtos = new ArrayList<>();
        if (entities != null) {
            for (AttachEntity entity : entities) {
                dtos.add(attachService.toDTO(entity));
            }
        }
        return dtos;
    }

    /**
     * Yordamchi metod (Entity topish)
     */
    public DoctorDetailsEntity getEntity(Long id) {
        return doctorDetailsRepository.findById(id)
                .orElseThrow(() -> new AppBadException("Doctor details topilmadi"));
    }
}
