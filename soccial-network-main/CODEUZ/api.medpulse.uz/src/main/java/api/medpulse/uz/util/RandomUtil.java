package api.medpulse.uz.util;

import java.util.Random;

public class RandomUtil {
    public static final Random random = new Random();
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public static String getRandomSmsCode() {
        return String.valueOf(random.nextInt(10000,99999));
    }

    // 6 xonali Short ID generatsiya qilish
    public static String generateQrToken() {
        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString(); // Masalan: "aB9x2Z"
    }
}
