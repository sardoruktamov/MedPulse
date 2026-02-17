package api.medpulse.uz.enums;

public enum ActionType {
    // User boshqaruvi
    BLOCK_USER,
    UNBLOCK_USER,
    DELETE_USER, // (Soft delete - visible=false)

    // Doktor boshqaruvi
    APPROVE_DOCTOR,
    REJECT_DOCTOR,

    // Rol boshqaruvi
    GRANT_ROLE_ADMIN,
    REMOVE_ROLE_ADMIN,
    GRANT_ROLE_DOCTOR,

    // Muhim ko'rishlar (Privacy)
    VIEW_PATIENT_HISTORY, // Bemor tarixini ko'rish
    VIEW_PATIENT_PROFILE,  // Bemor profilini ko'rish

    // Hackerlar uchun qopqon
    SECURITY_BREACH_ATTEMPT, // Jiddiy xavfsizlik buzilishi (Xakerlik gumoni)
    LOGIN_FAILED,            // Kirishda xatolik (Parol noto'g'ri)
    UNAUTHORIZED_ACCESS      // Ruxsat yo'q joyga kirish (403)
}
