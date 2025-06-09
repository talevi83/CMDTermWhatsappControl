package whatsappcmd.security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.*;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Scanner;

public class PasswordManager {
    private static final String KEY_FILE = System.getProperty("user.home") + "/.whatsappcmd/key.dat";
    private static final String SALT_FILE = System.getProperty("user.home") + "/.whatsappcmd/salt.dat";
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;
    private static SecretKey key = null;

    public static String getPassword() {
        // First try to get password from encrypted storage
        String storedPassword = getStoredPassword();
        if (storedPassword != null) {
            return storedPassword;
        }

        // If no stored password, prompt user
        return promptForPassword();
    }

    private static String promptForPassword() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Please enter your sudo password: ");
        String password = scanner.nextLine();
        
        // Ask if user wants to save the password
        System.out.print("Would you like to save this password securely? (y/n): ");
        String response = scanner.nextLine().toLowerCase();
        
        if (response.equals("y") || response.equals("yes")) {
            try {
                savePassword(password);
                System.out.println("Password saved securely.");
            } catch (Exception e) {
                System.err.println("Failed to save password: " + e.getMessage());
            }
        }
        
        return password;
    }

    private static void savePassword(String password) throws Exception {
        // Generate a random salt
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        // Create the key
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        SecretKey tmpKey = factory.generateSecret(spec);
        key = new SecretKeySpec(tmpKey.getEncoded(), "AES");

        // Encrypt the password
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedPassword = cipher.doFinal(password.getBytes());

        // Create directory if it doesn't exist
        Files.createDirectories(Paths.get(KEY_FILE).getParent());

        // Save the encrypted password and salt
        Files.write(Paths.get(KEY_FILE), Base64.getEncoder().encode(encryptedPassword));
        Files.write(Paths.get(SALT_FILE), salt);
    }

    private static String getStoredPassword() {
        try {
            if (!Files.exists(Paths.get(KEY_FILE)) || !Files.exists(Paths.get(SALT_FILE))) {
                return null;
            }

            // Read the salt and encrypted password
            byte[] salt = Files.readAllBytes(Paths.get(SALT_FILE));
            byte[] encryptedPassword = Base64.getDecoder().decode(Files.readAllBytes(Paths.get(KEY_FILE)));

            // Recreate the key
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            PBEKeySpec spec = new PBEKeySpec("dummy".toCharArray(), salt, ITERATIONS, KEY_LENGTH);
            SecretKey tmpKey = factory.generateSecret(spec);
            key = new SecretKeySpec(tmpKey.getEncoded(), "AES");

            // Decrypt the password
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decryptedPassword = cipher.doFinal(encryptedPassword);

            return new String(decryptedPassword);
        } catch (Exception e) {
            System.err.println("Error reading stored password: " + e.getMessage());
            return null;
        }
    }

    public static void clearStoredPassword() {
        try {
            Files.deleteIfExists(Paths.get(KEY_FILE));
            Files.deleteIfExists(Paths.get(SALT_FILE));
        } catch (IOException e) {
            System.err.println("Error clearing stored password: " + e.getMessage());
        }
    }
} 