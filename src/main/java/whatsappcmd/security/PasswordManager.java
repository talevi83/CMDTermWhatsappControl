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

public class PasswordManager {
    private static final String KEY_FILE = System.getProperty("user.home") + "/.whatsappcmd/key.dat";
    private static final String SALT_FILE = System.getProperty("user.home") + "/.whatsappcmd/salt.dat";
    private static final String MASTER_KEY_FILE = System.getProperty("user.home") + "/.whatsappcmd/master.key";
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;
    private static String masterKey = null;

    public static String getPassword() {
        // First try to get password from encrypted storage
        String storedPassword = getStoredPassword();
        if (storedPassword != null) {
            return storedPassword;
        }

        // If no stored password exists, we can't proceed
        throw new IllegalStateException("No sudo password has been set up. Please use the 'setup' command first.");
    }

    public static String setupPassword(String newPassword) {
        try {
            // Generate a master key if it doesn't exist
            if (masterKey == null) {
                if (Files.exists(Paths.get(MASTER_KEY_FILE))) {
                    masterKey = new String(Files.readAllBytes(Paths.get(MASTER_KEY_FILE)));
                } else {
                    // Generate a new master key
                    SecureRandom random = new SecureRandom();
                    byte[] keyBytes = new byte[32];
                    random.nextBytes(keyBytes);
                    masterKey = Base64.getEncoder().encodeToString(keyBytes);
                    
                    // Save the master key
                    Files.createDirectories(Paths.get(MASTER_KEY_FILE).getParent());
                    Files.write(Paths.get(MASTER_KEY_FILE), masterKey.getBytes());
                }
            }

            // Generate a random salt
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);

            // Create the key using the master key
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            PBEKeySpec spec = new PBEKeySpec(masterKey.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
            SecretKey tmpKey = factory.generateSecret(spec);
            SecretKey key = new SecretKeySpec(tmpKey.getEncoded(), "AES");

            // Encrypt the password
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedPassword = cipher.doFinal(newPassword.getBytes());

            // Create directory if it doesn't exist
            Files.createDirectories(Paths.get(KEY_FILE).getParent());

            // Save the encrypted password and salt
            Files.write(Paths.get(KEY_FILE), Base64.getEncoder().encode(encryptedPassword));
            Files.write(Paths.get(SALT_FILE), salt);

            return "Password has been set up successfully.";
        } catch (Exception e) {
            throw new RuntimeException("Failed to set up password: " + e.getMessage());
        }
    }

    private static String getStoredPassword() {
        try {
            if (!Files.exists(Paths.get(KEY_FILE)) || !Files.exists(Paths.get(SALT_FILE)) || !Files.exists(Paths.get(MASTER_KEY_FILE))) {
                return null;
            }

            // Read the master key
            if (masterKey == null) {
                masterKey = new String(Files.readAllBytes(Paths.get(MASTER_KEY_FILE)));
            }

            // Read the salt and encrypted password
            byte[] salt = Files.readAllBytes(Paths.get(SALT_FILE));
            byte[] encryptedPassword = Base64.getDecoder().decode(Files.readAllBytes(Paths.get(KEY_FILE)));

            // Create the key using the master key
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            PBEKeySpec spec = new PBEKeySpec(masterKey.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
            SecretKey tmpKey = factory.generateSecret(spec);
            SecretKey key = new SecretKeySpec(tmpKey.getEncoded(), "AES");

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

    public static String clearStoredPassword() {
        try {
            Files.deleteIfExists(Paths.get(KEY_FILE));
            Files.deleteIfExists(Paths.get(SALT_FILE));
            Files.deleteIfExists(Paths.get(MASTER_KEY_FILE));
            masterKey = null;
            return "Stored password and master key cleared successfully.";
        } catch (IOException e) {
            throw new RuntimeException("Error clearing stored password: " + e.getMessage());
        }
    }

    public static boolean isPasswordSet() {
        return Files.exists(Paths.get(KEY_FILE)) && 
               Files.exists(Paths.get(SALT_FILE)) && 
               Files.exists(Paths.get(MASTER_KEY_FILE));
    }
} 