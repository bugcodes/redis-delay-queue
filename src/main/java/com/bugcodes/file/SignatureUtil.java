package com.bugcodes.file;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * @author zbj
 * @date 2025/4/30
 */
public class SignatureUtil {

    private static final String SECRET = "BugCodesSecretKey";

    public static String generateToken(String fileName, long expireTimestamp) {
        try {
            String data = fileName + "|" + expireTimestamp;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(SECRET.getBytes(), "HmacSHA256"));
            byte[] sign = mac.doFinal(data.getBytes());
            return Base64.getUrlEncoder().withoutPadding().encodeToString(sign);
        } catch (Exception e) {
            throw new RuntimeException("Token生成失败", e);
        }
    }

    public static boolean verifyToken(String fileName, long expireTimestamp, String token) {
        String expected = generateToken(fileName, expireTimestamp);
        return expected.equals(token);
    }
}
