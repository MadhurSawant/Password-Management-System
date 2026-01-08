package Server;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class hashing {

    // Generate a random salt
    public static String generateSalt() {
        SecureRandom sr = new SecureRandom();
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    // Hash the password with SHA-256 and salt
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String saltedPassword = salt + password;
            byte[] hash = md.digest(saltedPassword.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    // Main method for demonstration
    public static void main(String[] args) {
        String password = "mypassword123";
        String salt = generateSalt();

        // Hash password with generated salt
        String hashed = hashPassword(password, salt);
        System.out.println("Salt: " + salt);
        System.out.println("Hashed Password: " + hashed);

        // Simulate verifying password
        String inputPassword = "mypassword123";
        String inputHash = hashPassword(inputPassword, salt);

        boolean isMatch = hashed.equals(inputHash);
        System.out.println("Password match? " + isMatch);
    }
}
