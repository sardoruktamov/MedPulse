package api.medpulse.uz.service;

import api.medpulse.uz.dto.AppResponse;
import api.medpulse.uz.dto.AttachDTO;
import api.medpulse.uz.dto.HealthRecord.*;
import api.medpulse.uz.entity.AttachEntity;
import api.medpulse.uz.entity.HealthRecordEntity;
import api.medpulse.uz.entity.PatientProfileEntity;
import api.medpulse.uz.enums.ProfileRole;
import api.medpulse.uz.exps.AppBadException;
import api.medpulse.uz.repository.HealthRecordRepository;
import api.medpulse.uz.repository.PatientProfileRepository;
import api.medpulse.uz.util.SpringSecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class HealthRecordService {

    @Value("${attach.upload.url}")
    private String attachUrl;

    @Autowired
    private HealthRecordRepository healthRecordRepository;
    @Autowired
    private PatientProfileRepository patientProfileRepository;

    @Autowired
    private AttachService attachService;

    @Autowired
    private AccessControlService accessControlService;

    public HealthRecordDTO create(HealthRecordCreateDTO dto) {
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

        if (dto.getPhotoIds() != null && !dto.getPhotoIds().isEmpty()) {
            List<AttachEntity> attachList = new ArrayList<>();
            for (String photoId : dto.getPhotoIds()) {
                // Har bir ID bo'yicha Entityni olamiz
                // (AttachService da getEntity(String id) metodi bo'lishi kerak)
                attachList.add(attachService.getEntity(photoId));
            }
            entity.setPhotos(attachList);
        }

        healthRecordRepository.save(entity);
        return toDTO(entity);
    }

    /**
     * 3. Tahrirlash (Update)
     */
    public HealthRecordDTO update(Long id, HealthRecordUpdateDTO dto) {
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


        // --- ESKI RASMLARNI TOZALASH 🔥 ---
        // Agar bo'sh ro'yxat [] kelsa, rasmlarni o'chiramiz.
        // Agar to'la ro'yxat kelsa, yangisiga almashtiramiz.

        // Agar dto.getPhotoIds() null kelsa, demak rasmlarga tegilmasin degani -> hech nima qilmaymiz.
        if (dto.getPhotoIds() != null) {

            // 1. O'chirilishi kerak bo'lgan rasmlarni aniqlab olamiz
            // (Eskida bor, lekin Yangida yo'q bo'lgan rasmlar)
            List<String> oldPhotoIds = new ArrayList<>();
            if (entity.getPhotos() != null) {
                oldPhotoIds = entity.getPhotos().stream().map(AttachEntity::getId).toList();
            }

            List<String> newPhotoIds = dto.getPhotoIds(); // Frontenddan kelgan

            // 2. Yangi rasmlarni Entityga o'rnatamiz
            List<AttachEntity> newEntityList = new ArrayList<>();
            for (String photoId : newPhotoIds) {
                newEntityList.add(attachService.getEntity(photoId));
            }
            entity.setPhotos(newEntityList);

            // 3. Keraksiz rasmlarni Diskdan o'chiramiz
            for (String oldId : oldPhotoIds) {
                // Agar eski ID yangi ro'yxatda bo'lmasa -> O'CHIRAMIZ
                if (!newPhotoIds.contains(oldId)) {
                    try {
                        attachService.delete(oldId); // Diskdan va Attach jadvalidan o'chadi
                    } catch (Exception e) {
                        // Agar rasm o'chmay qolsa, dastur to'xtab qolmasligi kerak
                        // Shunchaki logga yozib qo'yamiz
                        log.warn("Rasmni o'chirishda xatolik: {}", oldId);
                    }
                }
            }
        }

        if (dto.getDiseaseName() != null) entity.setDiseaseName(dto.getDiseaseName());
        if (dto.getRecordDate() != null) entity.setRecordDate(dto.getRecordDate());
        if (dto.getDoctorName() != null) entity.setDoctorName(dto.getDoctorName());
        if (dto.getHospitalName() != null) entity.setHospitalName(dto.getHospitalName());
        if (dto.getTreatment() != null) entity.setTreatment(dto.getTreatment());
        if (dto.getNote() != null) entity.setNote(dto.getNote());
        if (dto.getIsCritical() != null) entity.setIsCritical(dto.getIsCritical());

        healthRecordRepository.save(entity);

        return toDTO(entity);
    }

    /**
     * GET ONE (Batafsil ko'rish)
     */
    public HealthRecordDTO get(Long id) {
        // 1. Joriy foydalanuvchi
        Integer currentUserId = SpringSecurityUtil.getCurrentUserId();

        // 2. Yozuvni topish
        HealthRecordEntity entity = healthRecordRepository.findById(id)
                .orElseThrow(() -> new AppBadException("Ma'lumot topilmadi"));

        // 3. XAVFSIZLIK: Bu yozuv rostdan ham shu odamning oilasiga tegishlimi?
        // Agar begona bo'lsa, xato beramiz!
        if (!entity.getPatient().getOwner().getId().equals(currentUserId)) {
            throw new AppBadException("Bu ma'lumotni ko'rishga ruxsatingiz yo'q");
        }

        // 4. DTO ga o'girib qaytarish (Bunda hamma fieldlar bo'ladi)
        return toDTO(entity);
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

    public List<HealthRecordSearchDTO> filter(HealthFilterDTO filter) {
        Integer currentUserId = SpringSecurityUtil.getCurrentUserId();

        // 1. Sana logikasi
        LocalDate fromDate = filter.getFromDate();
        LocalDate toDate = filter.getToDate();
        if (fromDate != null && toDate == null) {
            toDate = LocalDate.now(); // Agar tugash sanasi bo'lmasa, bugun deb olamiz
        }

        // 2. Matn logikasi (bo'sh joylarni tozalash)
        String searchText = filter.getText();
        if (searchText != null && searchText.trim().isEmpty()) {
            searchText = null;
        } else if (searchText != null) {
            // DIQQAT: .toLowerCase() ni shu yerda qilamiz!
            // Endi "Grip" -> "%grip%" bo'ladi
            searchText = "%" + searchText.trim().toLowerCase() + "%";
        }

        // 3. Bazadan ma'lumot olish
        List<HealthRecordSearchDTO> resultList = healthRecordRepository.filter(currentUserId, searchText, fromDate, toDate);

        // 4. URL YASASH (Post-processing)
        // Har bir natijani aylanib chiqib, ID o'rniga to'liq URL qo'yamiz
        resultList.forEach(dto -> {
            if (dto.getPatientPhotoUrl() != null) {
                // Masalan: "http://localhost:8080/api/v1/attach" + "/open/" + "uuid.jpg"
                String fullUrl = attachUrl + "/open/" + dto.getPatientPhotoUrl();
                dto.setPatientPhotoUrl(fullUrl);
            }
        });

        return resultList;
    }

    // Ro'yxatni olish
    public List<HealthRecordDTO> getMedicalHistory(String patientId) {
        Integer currentUserId = SpringSecurityUtil.getCurrentUserId();

        // 1. Avval "Bu user Bemorning egasimi?" deb tekshiramiz
        boolean isOwner = patientProfileRepository.findByIdAndOwner_Id(patientId, currentUserId).isPresent();

        // 2. MANTIQNI BIRLASHTIRAMIZ
        if (isOwner) {
            // A) Agar OTA bo'lsa -> HECH NARSA QILISH SHART EMAS, to'g'ri pastga tushib ketaveradi.
            // (Chunki ota o'z bolasini doim ko'ra olishi kerak)
        } else {
            // B) Agar OTA BO'LMASA -> Demak bu begona odam yoki Doktor.

            if (SpringSecurityUtil.hazRole(ProfileRole.ROLE_DOCTOR)) {
                // Agar Doktor bo'lsa -> Ruxsatnomani tekshiramiz ("Qorovul")
                accessControlService.checkDoctorAccess(patientId);
                // Agar checkDoctorAccess exception otmasa, demak ruxsat bor va pastga o'tadi.
            } else {
                // C) Agar na Ota va na Doktor bo'lsa -> Begona shaxs.
                throw new AppBadException("Ruxsat yo'q. Bu ma'lumot maxfiy.");
            }
        }

        // 3. Ma'lumotni olib qaytaramiz
        List<HealthRecordEntity> list = healthRecordRepository.findByPatientIdOrderByRecordDateDesc(patientId);

        return list.stream()
                .map(this::toDTO)
                .toList();
    }

    // 1. Convert Metodi (Entity -> DTO)
    public HealthRecordDTO toDTO(HealthRecordEntity entity) {
        HealthRecordDTO dto = new HealthRecordDTO();
        dto.setId(entity.getId());
        dto.setDiseaseName(entity.getDiseaseName());
        dto.setRecordDate(entity.getRecordDate());
        dto.setDoctorName(entity.getDoctorName());
        dto.setHospitalName(entity.getHospitalName());
        dto.setTreatment(entity.getTreatment());
        dto.setNote(entity.getNote());
        dto.setIsCritical(entity.getIsCritical());
        dto.setCreatedDate(entity.getCreatedDate());

        // Rasm konvertatsiyasi: Entity -> DTO (URL bilan)
        if (entity.getPhotos() != null && !entity.getPhotos().isEmpty()) {
            List<AttachDTO> photoDTOs = new ArrayList<>();
            for (AttachEntity photoEntity : entity.getPhotos()) {
                photoDTOs.add(attachService.toDTO(photoEntity));
            }
            dto.setPhotos(photoDTOs);
        }

        return dto;
    }
}
