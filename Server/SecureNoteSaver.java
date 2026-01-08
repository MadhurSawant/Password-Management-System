package Server;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class SecureNoteSaver {

    private static final String SAVE_DIRECTORY = "";
    private static final String ENCRYPTION_KEY = ""; // 16-char AES key
    private static final String ALGORITHM = "AES";

    public static Map<String, String> saveNote(String title, String content) {
        Map<String, String> result = new HashMap<>();

        try {
            // Ensure the directory exists
            Files.createDirectories(Paths.get(SAVE_DIRECTORY));

            // Sanitize title to create a safe filename
            String safeTitle = title.replaceAll("[^a-zA-Z0-9\\-_]", "_");
            String filename = safeTitle + ".txt";
            String filePath = SAVE_DIRECTORY + filename;

            // Encrypt the content
            String encryptedContent = encrypt1(content);
            if (encryptedContent == null || encryptedContent.isEmpty()) {
                System.err.println("Encryption failed");
                return null;
            }

            // Write encrypted content to file
            Files.write(Paths.get(filePath), encryptedContent.getBytes());

            // Populate result map
            result.put("filename", filename);
            result.put("filepath", filePath);

            return result;

        } catch (Exception e) {
            System.err.println("Error saving secure note: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static String encrypt1(String plainText) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(ENCRYPTION_KEY.getBytes("UTF-8"), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decrypt1(String encryptedText) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(ENCRYPTION_KEY.getBytes("UTF-8"), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
        return new String(decryptedBytes, "UTF-8");
    }
}
