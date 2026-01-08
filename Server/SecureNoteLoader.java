package Server;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

public class SecureNoteLoader {

    private static final String ENCRYPTION_KEY = ""; // Must be 16/24/32 chars for AES

    /**
     * Loads and decrypts the content of a secure note from the given file path.
     * @param filePath The full path to the encrypted note file.
     * @return Decrypted content as a String, or null if error.
     */
    public static String loadDecryptedNote(String filePath) {
        try {
            Path path = Paths.get(filePath);

            if (!Files.exists(path)) {
                System.err.println("Error: File does not exist - " + filePath);
                return null;
            }

            byte[] encryptedBytes = Files.readAllBytes(path);
            String encryptedContent = new String(encryptedBytes, StandardCharsets.UTF_8);

            // Decrypt the content
            String decryptedContent = SecureNoteSaver.decrypt1(encryptedContent);
            return decryptedContent;

        } catch (Exception e) {
            System.err.println("Error loading or decrypting note: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
