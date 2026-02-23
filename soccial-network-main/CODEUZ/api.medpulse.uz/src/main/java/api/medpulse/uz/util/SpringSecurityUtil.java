package api.medpulse.uz.util;


import api.medpulse.uz.config.CustomUserDetails;
import api.medpulse.uz.enums.ProfileRole;
import api.medpulse.uz.exps.AppBadException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SpringSecurityUtil {

    public static CustomUserDetails getCurrentProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // HIMOYA: Agar user login qilmagan bo'lsa, null qaytaramiz
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return null;
        }

        return (CustomUserDetails) authentication.getPrincipal();
    }

    public static Integer getCurrentUserId() {
        CustomUserDetails userDetails = getCurrentProfile();
        // HIMOYA: Agar null bo'lsa, darhol xato otamiz
        if (userDetails == null) {
            throw new AppBadException("Tizimga kirish talab etiladi!");
        }
        return userDetails.getId();
    }

    public static boolean hazRole(ProfileRole role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(pr -> pr.getAuthority().equals(role.name()));
    }

    // foydalanuvchini agar 1 ta roli bolsa shuni olish
//    public static String getCurrentUserRole() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null && authentication.getAuthorities() != null && !authentication.getAuthorities().isEmpty()) {
//            return authentication.getAuthorities().iterator().next().getAuthority();
//        }
//        return null;
//    }
}
