package Server;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.kerberos.EncryptionKey;
import java.util.Base64;

public class AESExample {

    private static final String ALGORITHM = "AES/ECB/PKCS5Padding";
    public static String Encryption_Key = "madhur123";
    // Valid AES key must be 16 bytes
    private static byte[] fixKey(String key) {
        byte[] keyBytes = new byte[16];
        byte[] inputBytes = key.getBytes();

        // Fill keyBytes with input, truncate or pad as needed
        int len = Math.min(inputBytes.length, keyBytes.length);
        System.arraycopy(inputBytes, 0, keyBytes, 0, len);
        return keyBytes;
    }

    public static String encrypt(String plainText) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(fixKey(Encryption_Key), "AES");
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decrypt(String encryptedText) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(fixKey(Encryption_Key), "AES");
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
        return new String(decryptedBytes, "UTF-8");
    }

    public static void main(String[] args) throws Exception {

    }
}

