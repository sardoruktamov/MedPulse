package api.medpulse.uz.service;

import api.medpulse.uz.entity.ProfileEntity;
import api.medpulse.uz.entity.ProfileRoleEntity;
import api.medpulse.uz.enums.GeneralStatus;
import api.medpulse.uz.enums.ProfileRole;
import api.medpulse.uz.exps.AppBadException;
import api.medpulse.uz.repository.ProfileRepository;
import api.medpulse.uz.repository.ProfileRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final ProfileRepository profileRepository;
    private final ProfileRoleRepository profileRoleRepository;

    /**
     * Foydalanuvchiga ADMIN rolini berish.
     * Faqat SUPER_ADMIN chaqira oladi.
     */
    public String assignAdminRole(Integer targetUserId) {
        // 1. O'zgartirilayotgan user haqiqatan bormi?
        ProfileEntity targetProfile = profileRepository.findById(targetUserId)
                .orElseThrow(() -> new AppBadException("Foydalanuvchi topilmadi"));

        // 2. Foydalanuvchi aktivmi? (Bloklangan odamni Admin qilib bo'lmaydi)
        if (!targetProfile.getVisible() || !GeneralStatus.ACTIVE.equals(targetProfile.getStatus())) {
            throw new AppBadException("Foydalanuvchi bloklangan yoki mavjud emas");
        }

        // 3. IDEMPOTENCY: U allaqachon Admin emasmi?
        // Agar tekshirmasak, bitta odamda 10 ta ROLE_ADMIN qatori paydo bo'lishi mumkin.
        boolean isAlreadyAdmin = profileRoleRepository.existsByProfileIdAndRoles(targetUserId, ProfileRole.ROLE_ADMIN);
        if (isAlreadyAdmin) {
            throw new AppBadException("Bu foydalanuvchi allaqachon ADMIN huquqiga ega");
        }

        // 4. Rolni yaratish va saqlash
        ProfileRoleEntity newRole = new ProfileRoleEntity();
        newRole.setProfileId(targetUserId); // Entityni o'zini set qilish shart emas, ID yetadi
        newRole.setRoles(ProfileRole.ROLE_ADMIN);
        newRole.setCreatedDate(LocalDateTime.now());

        profileRoleRepository.save(newRole);

        log.info("SUPER_ADMIN tomonidan user ID={} ga ADMIN roli berildi", targetUserId);
        return "Muvaffaqiyatli! Foydalanuvchi endi tizim administratori.";
    }

    /**
     * Admindan huquqni olib tashlash (Downgrade to USER).
     * Faqat SUPER_ADMIN qila oladi.
     */
    public String removeAdminRole(Integer targetUserId) {
        // 1. User bormi?
        ProfileEntity targetProfile = profileRepository.findById(targetUserId)
                .orElseThrow(() -> new AppBadException("Foydalanuvchi topilmadi"));

        // 2. Unda haqiqatan ham ADMIN roli bormi?
        boolean isAdmin = profileRoleRepository.existsByProfileIdAndRoles(targetUserId, ProfileRole.ROLE_ADMIN);
        if (!isAdmin) {
            throw new AppBadException("Bu foydalanuvchi Admin emas, uning rolini olib bo'lmaydi.");
        }

        // 3. XAVFSIZLIK: SUPER_ADMIN o'zini o'zi yoki boshqa SUPER_ADMINni o'chira olmasligi kerak!
        // (Agar kelajakda bir nechta SuperAdmin bo'lsa)
        boolean isSuperAdmin = profileRoleRepository.existsByProfileIdAndRoles(targetUserId, ProfileRole.ROLE_SUPERADMIN);
        if (isSuperAdmin) {
            throw new AppBadException("Super Adminni oddiy user qilib bo'lmaydi!");
        }

        // 4. Rolni o'chiramiz
        profileRoleRepository.deleteByProfileIdAndRoles(targetUserId, ProfileRole.ROLE_ADMIN);

        log.warn("SUPER_ADMIN tomonidan user ID={} dan ADMIN huquqi olib tashlandi", targetUserId);
        return "Muvaffaqiyatli! Admin huquqi bekor qilindi, endi u oddiy foydalanuvchi.";
    }
}
