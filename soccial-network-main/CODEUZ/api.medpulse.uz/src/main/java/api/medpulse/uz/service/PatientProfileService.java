package api.medpulse.uz.service;

import api.medpulse.uz.dto.patient.PatientCreateDTO;
import api.medpulse.uz.dto.patient.PatientProfileDTO;
import api.medpulse.uz.dto.patient.PatientUpdateDTO;
import api.medpulse.uz.dto.post.PostDTO;
import api.medpulse.uz.entity.*;
import api.medpulse.uz.enums.ProfileRole;
import api.medpulse.uz.exps.AppBadException;
import api.medpulse.uz.repository.DistrictRepository;
import api.medpulse.uz.repository.PatientProfileRepository;
import api.medpulse.uz.repository.ProfileRepository;
import api.medpulse.uz.repository.RegionRepository;
import api.medpulse.uz.util.RandomUtil;
import api.medpulse.uz.util.SpringSecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.security.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientProfileService {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private PatientProfileRepository patientProfileRepository;
    @Autowired
    private AttachService attachService;

    @Autowired
    private RegionRepository regionRepository;
    @Autowired
    private DistrictRepository districtRepository;
    @Autowired
    private AccessControlService accessControlService;

    // Profilni yangilash
    public PatientProfileDTO update(String profileId, PatientUpdateDTO dto) {
        // 1. Hozirgi kirgan foydalanuvchi (Ota) ID sini olamiz
        Integer currentUserId = SpringSecurityUtil.getCurrentUserId();

        // 2. Profilni qidiramiz: ID si bo'yicha VA egasi shu odam ekanligi bo'yicha
        PatientProfileEntity entity = patientProfileRepository.findByIdAndOwner_Id(profileId, currentUserId)
                .orElseThrow(() -> new AppBadException(
                        "Profile not found or access denied/Profil topilmadi yoki kirish taqiqlandi"));

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
        if (dto.getFullName() != null)
            entity.setFullName(dto.getFullName());
        if (dto.getBirthDate() != null)
            entity.setBirthDate(dto.getBirthDate());
        if (dto.getGender() != null)
            entity.setGender(dto.getGender());

        // Tibbiy qism
        if (dto.getBloodGroup() != null)
            entity.setBloodGroup(dto.getBloodGroup()); // Enum bo'lsa .name() shart emas
        if (dto.getWeight() != null)
            entity.setWeight(dto.getWeight());
        if (dto.getHeight() != null)
            entity.setHeight(dto.getHeight());
        if (dto.getWorkingBloodPressure() != null)
            entity.setWorkingBloodPressure(dto.getWorkingBloodPressure());
        // Token generatsiya qilish
        entity.setQrToken(RandomUtil.generateQrToken());

        // 1. Allergiyani yangilash
        if (dto.getAllergies() != null) {
            entity.setAllergies(dto.getAllergies());
        }

        // 2. Favqulodda bog'lanish shaxsini yangilash
        if (dto.getEmergencyContactName() != null) {
            entity.setEmergencyContactName(dto.getEmergencyContactName());
        }

        // 3. Favqulodda telefon raqamni yangilash
        if (dto.getEmergencyContactPhone() != null) {
            // Telefon raqamni formatlash yoki tozalash kerak bo'lsa shu yerda qilinadi
            entity.setEmergencyContactPhone(dto.getEmergencyContactPhone());
        }

        entity.setAddress(dto.getAddress()); // Ko'cha nomi

        // 4. VILOYATNI YANGILASH (Agar ID kelgan bo'lsa)
        if (dto.getRegionId() != null) {
            RegionEntity region = regionRepository.findById(dto.getRegionId())
                    .orElseThrow(() -> new AppBadException("Viloyat topilmadi"));
            entity.setRegion(region);
        }

        // 5. TUMANNI YANGILASH (Agar ID kelgan bo'lsa)
        if (dto.getDistrictId() != null) {
            DistrictEntity district = districtRepository.findById(dto.getDistrictId())
                    .orElseThrow(() -> new AppBadException("Tuman topilmadi"));

            // Tekshiruv: Tanlangan tuman haqiqatan ham shu viloyatdami?
            // Bu ma'lumotlar butunligi uchun kerak
            if (dto.getRegionId() != null && !district.getRegion().getId().equals(dto.getRegionId())) {
                throw new AppBadException("Tanlangan tuman bu viloyatga tegishli emas!");
            }

            entity.setDistrict(district);
        }

        // 4. Saqlash
        patientProfileRepository.save(entity);
        // 2. Agar rasm o'zgargan bo'lsa, eski rasmni AttachService orqali o'chiramiz
        if (deletePhotoId != null) {
            attachService.delete(deletePhotoId);
        }

        return toDTO(entity);
    }

    public PatientProfileDTO create(PatientCreateDTO dto) {
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
        if (dto.getPhotoId() != null)
            entity.setPhotoId(dto.getPhotoId());
        if (dto.getBloodGroup() != null)
            entity.setBloodGroup(dto.getBloodGroup());
        if (dto.getWeight() != null)
            entity.setWeight(dto.getWeight());
        if (dto.getHeight() != null)
            entity.setHeight(dto.getHeight());
        if (dto.getWorkingBloodPressure() != null)
            entity.setWorkingBloodPressure(dto.getWorkingBloodPressure());
        // Token generatsiya qilish
        entity.setQrToken(RandomUtil.generateQrToken());

        // 1. Allergiyani yangilash
        if (dto.getAllergies() != null) {
            entity.setAllergies(dto.getAllergies());
        }

        // 2. Favqulodda bog'lanish shaxsini yangilash
        if (dto.getEmergencyContactName() != null) {
            entity.setEmergencyContactName(dto.getEmergencyContactName());
        }

        // 3. Favqulodda telefon raqamni yangilash
        if (dto.getEmergencyContactPhone() != null) {
            // Telefon raqamni formatlash yoki tozalash kerak bo'lsa shu yerda qilinadi
            entity.setEmergencyContactPhone(dto.getEmergencyContactPhone());
        }
        // 5. Saqlash
        patientProfileRepository.save(entity);
        return toDTO(entity);
    }

    // Foydalanuvchining barcha profillarini olish (O'ziniki va oilasiniki)
    // Bu metod frontendga qaysi ID ni update qilish kerakligini bilish uchun kerak
    // GET MY FAMILY
    public List<PatientProfileDTO> getMyFamilyProfiles() {
        Integer currentUserId = SpringSecurityUtil.getCurrentUserId();
        List<PatientProfileEntity> list = patientProfileRepository.findByOwner_Id(currentUserId);

        // Stream orqali hamma entitylarni DTO ga o'giramiz
        return list.stream()
                .map(this::toDTO)
                .toList();
    }

    public PatientProfileDTO getById(String id) {
        // 1. Bemorni bazadan qidiramiz
        PatientProfileEntity entity = patientProfileRepository.findById(id)
                .orElseThrow(() -> new AppBadException("Bemor topilmadi"));

        Integer currentUserId = SpringSecurityUtil.getCurrentUserId();

        // 2. TEKSHIRUV: So'rov qilayotgan odam shu bemorning OTASI (Owner) ekanligini
        // tekshiramiz
        boolean isOwner = entity.getOwner().getId().equals(currentUserId);

        // 3. MANTIQ:
        if (isOwner) {
            // A) Agar OTA bo'lsa -> To'siqsiz ruxsat
            return toDTO(entity);
        } else if (SpringSecurityUtil.hazRole(ProfileRole.ROLE_DOCTOR)) {
            // B) Agar DOKTOR bo'lsa -> Ruxsatnomasi (Access) borligini tekshiramiz
            // Agar ruxsati bo'lmasa, checkDoctorAccess() metodi Exception otadi va kod shu
            // yerda to'xtaydi.
            accessControlService.checkDoctorAccess(id);

            // Agar exception otmasa, demak ruxsat bor
            return toDTO(entity);
        }

        // 4. Agar Ota ham, Ruxsatli Doktor ham bo'lmasa (Admin, SuperAdmin, Begona
        // User)
        // Qat'iy rad etamiz!
        throw new AppBadException("Sizda bu bemor ma'lumotlarini ko'rishga ruxsat yo'q!");
    }

    // 1. Convert Metodi (Yordamchi metod)
    public PatientProfileDTO toDTO(PatientProfileEntity entity) {
        PatientProfileDTO dto = new PatientProfileDTO();
        dto.setId(entity.getId());
        dto.setFullName(entity.getFullName());
        dto.setBirthDate(entity.getBirthDate());
        dto.setGender(entity.getGender());

        // Rasm konvertatsiyasi: Entity -> DTO (URL bilan)
        if (entity.getPhoto() != null) {
            dto.setPhoto(attachService.toDTO(entity.getPhoto()));
        }

        dto.setBloodGroup(entity.getBloodGroup());
        dto.setWeight(entity.getWeight());
        dto.setHeight(entity.getHeight());

        if (entity.getRegion() != null) {
            dto.setRegionId(entity.getRegion().getId());
        }
        if (entity.getDistrict() != null) {
            dto.setDistrictId(entity.getDistrict().getId());
        }
        dto.setAddress(entity.getAddress());

        dto.setAllergies(entity.getAllergies());
        dto.setEmergencyContactName(entity.getEmergencyContactName());
        dto.setEmergencyContactPhone(entity.getEmergencyContactPhone());
        dto.setWorkingBloodPressure(entity.getWorkingBloodPressure());
        return dto;
    }

}
