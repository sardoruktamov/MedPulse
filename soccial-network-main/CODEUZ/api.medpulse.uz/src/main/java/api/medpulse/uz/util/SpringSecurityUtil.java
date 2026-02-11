package api.medpulse.uz.util;


import api.medpulse.uz.config.CustomUserDetails;
import api.medpulse.uz.enums.ProfileRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SpringSecurityUtil {

    public static CustomUserDetails getCurrentProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        return user;
    }

    public static Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        return user.getId();
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
