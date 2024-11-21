package org.myproject.shortlink.admin.util;

import java.security.SecureRandom;

public class GroupIdGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateRandomString() {
        return generateRandomString(6);
    }

    /**
     * 生成指定长度的随机字符串，字符串只包含数字和字母。
     *
     * @param length 需要生成的字符串长度
     * @return 生成的随机字符串
     */
    public static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString();
    }
}
