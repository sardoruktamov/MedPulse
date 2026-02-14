package api.medpulse.uz.controller;

import api.medpulse.uz.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Tag(name = "Super Admin Panel", description = "Faqat Super Adminlar uchun maxsus API")
public class AdminController {

    private final AdminService adminService;

    // Eng muhim qism: Faqat SUPER_ADMIN kira olsin!
    // @PreAuthorize bu method darajasidagi xavfsizlik (Method Level Security)
    @PreAuthorize("hasRole('SUPERADMIN')")
    @PostMapping("/change-role/to-admin/{userId}")
    @Operation(summary = "User -> Admin", description = "Oddiy foydalanuvchiga ADMIN huquqini berish")
    public ResponseEntity<String> assignAdminRole(@PathVariable Integer userId) {
        return ResponseEntity.ok(adminService.assignAdminRole(userId));
    }

    // 2. Admin huquqini olib tashlash
    @PreAuthorize("hasRole('SUPERADMIN')")
    @PostMapping("/change-role/remove-admin/{userId}")
    @Operation(summary = "Admin -> User", description = "Admin huquqini bekor qilish va oddiy userga aylantirish")
    public ResponseEntity<String> removeAdminRole(@PathVariable Integer userId) {
        return ResponseEntity.ok(adminService.removeAdminRole(userId));
    }
}
