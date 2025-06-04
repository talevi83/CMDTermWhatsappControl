import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import org.openqa.selenium.By;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.Set;


public class WebDriverInitialize {

    private static final String COOKIE_FILE_NAME = "src/main/resources/whatsapp_cookies.json";
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";

    protected static WebDriver initializeWebDriver() {
        System.out.println("Closing all chrome instances before starting webdriver...");
        closeExistingChromeInstances();

        System.out.println("Setting up Chrome profile and options.");
        ChromeOptions options = setupChromeOptions();

        System.out.println("Opening chrome browser using Selenium.");
        WebDriver driver = new ChromeDriver(options);

        try {
            // Set implicit wait
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

            System.out.println("Navigate to web.whatsapp.com");
            driver.get("https://web.whatsapp.com");

            // Load cookies before refresh
            loadCookiesFromFile(driver);

            // Wait a bit before refresh
            Thread.sleep(2000);
            driver.navigate().refresh();

            // Wait for page to load and check if login is needed
            SeleniumUtils.waitForWhatsAppToLoad(driver);

            // Save cookies after successful load
            saveCookiesToFile(driver);

        } catch (Exception e) {
            System.err.println("Error during WebDriver initialization: " + e.getMessage());
            e.printStackTrace();
        }

        return driver;
    }

    private static ChromeOptions setupChromeOptions() {
        ChromeOptions options = new ChromeOptions();

        // Profile setup
        String profilePath = setupProfile();
        if (profilePath != null) {
            String userDataDir = new File(profilePath).getParent();
            String profileName = new File(profilePath).getName();

            options.addArguments("--user-data-dir=" + userDataDir);
            options.addArguments("--profile-directory=" + profileName);
        }

        // Essential arguments for stability
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-plugins");
        options.addArguments("--disable-images"); // Faster loading
        options.addArguments("--disable-javascript"); // Remove this if WhatsApp doesn't work
        options.addArguments("--disable-web-security");
        options.addArguments("--allow-running-insecure-content");
        options.addArguments("--ignore-certificate-errors");
        options.addArguments("--ignore-ssl-errors");
        options.addArguments("--ignore-certificate-errors-spki-list");

        // Anti-detection measures
        options.addArguments("--user-agent=" + USER_AGENT);
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);

        // Performance optimizations
        options.addArguments("--memory-pressure-off");
        options.addArguments("--max_old_space_size=4096");

        // Disable logging
        options.addArguments("--log-level=3");
        options.addArguments("--silent");

        return options;
    }

    private static String setupProfile() {
        try {
            // Source profile path - make this configurable
            Path sourceProfile = Paths.get("/Users/tallevi/Library/Application Support/Google/Chrome/Profile 1");

            // Destination profile path
            Path destProfile = Paths.get(System.getProperty("user.home") + "/selenium_profiles/whatsapp_profile");

            // Always refresh the profile to get latest data
            if (Files.exists(sourceProfile)) {
                System.out.println("Refreshing Chrome profile...");

                // Delete old profile if exists
                if (Files.exists(destProfile)) {
                    deleteDirectory(destProfile.toFile());
                }

                // Copy fresh profile
                copyDirectory(sourceProfile, destProfile);
                return destProfile.toString();
            } else {
                System.out.println("Source profile not found. Using default profile.");
                return null;
            }

        } catch (Exception e) {
            System.err.println("Error setting up profile: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private static void copyDirectory(Path source, Path destination) throws IOException {
        // Create parent directories
        Files.createDirectories(destination.getParent());

        Files.walk(source)
                .forEach(sourcePath -> {
                    try {
                        Path destPath = destination.resolve(source.relativize(sourcePath));
                        if (Files.isDirectory(sourcePath)) {
                            Files.createDirectories(destPath);
                        } else {
                            // Skip lock files and other temporary files
                            String fileName = sourcePath.getFileName().toString();
                            if (!fileName.contains("lock") && !fileName.contains("LOCK") &&
                                    !fileName.endsWith(".tmp") && !fileName.startsWith("~")) {
                                Files.copy(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING);
                            }
                        }
                    } catch (IOException e) {
                        // Continue copying other files even if one fails
                        System.err.println("Failed to copy: " + sourcePath + " - " + e.getMessage());
                    }
                });
    }

    private static void deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
            directory.delete();
        }
    }

    protected static void closeExistingChromeInstances() {
        try {
            String os = System.getProperty("os.name").toLowerCase();

            if (os.contains("mac")) {
                // More specific kill commands for macOS
                Runtime.getRuntime().exec("pkill -f 'Google Chrome'");
                Runtime.getRuntime().exec("pkill -f 'Chromium'");
                Runtime.getRuntime().exec("pkill -f 'chromedriver'");
            } else if (os.contains("windows")) {
                Runtime.getRuntime().exec("taskkill /f /im chrome.exe");
                Runtime.getRuntime().exec("taskkill /f /im chromedriver.exe");
            } else { // Linux
                Runtime.getRuntime().exec("pkill chrome");
                Runtime.getRuntime().exec("pkill chromium");
                Runtime.getRuntime().exec("pkill chromedriver");
            }

            Thread.sleep(3000); // Increased wait time
        } catch (Exception e) {
            System.err.println("Error closing Chrome instances: " + e.getMessage());
        }
    }

    protected static void saveCookiesToFile(WebDriver driver) {
        try {
            Set<Cookie> cookies = driver.manage().getCookies();

            // Create directory if it doesn't exist
            File cookieFile = new File(COOKIE_FILE_NAME);
            cookieFile.getParentFile().mkdirs();

            try (FileWriter writer = new FileWriter(COOKIE_FILE_NAME)) {
                new Gson().toJson(cookies, writer);
                System.out.println("Cookies saved successfully. Count: " + cookies.size());
            }
        } catch (Exception e) {
            System.err.println("Error saving cookies: " + e.getMessage());
            e.printStackTrace();
        }
    }

    protected static void loadCookiesFromFile(WebDriver driver) {
        try {
            File cookieFile = new File(COOKIE_FILE_NAME);
            if (!cookieFile.exists()) {
                System.out.println("Cookie file doesn't exist. Skipping cookie loading.");
                return;
            }

            try (FileReader reader = new FileReader(COOKIE_FILE_NAME)) {
                Type cookieSetType = new TypeToken<Set<Cookie>>(){}.getType();
                Set<Cookie> cookies = new Gson().fromJson(reader, cookieSetType);

                if (cookies != null && !cookies.isEmpty()) {
                    for (Cookie cookie : cookies) {
                        try {
                            // Validate cookie before adding
                            if (cookie.getDomain() != null && cookie.getName() != null) {
                                driver.manage().addCookie(cookie);
                            }
                        } catch (Exception e) {
                            System.err.println("Failed to add cookie: " + cookie.getName() + " - " + e.getMessage());
                        }
                    }
                    System.out.println("Cookies loaded successfully. Count: " + cookies.size());
                } else {
                    System.out.println("No valid cookies found in file.");
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading cookies: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Additional utility method to check if WhatsApp is properly connected
    public static boolean isWhatsAppConnected(WebDriver driver) {
        try {
            // Check if we can find the chat list or main interface
            return driver.findElements(By.xpath("//*[@data-testid='chat-list']")).size() > 0 ||
                    driver.findElements(By.xpath("//div[contains(@class, 'app-wrapper-web')]")).size() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    // Method to handle reconnection if needed
    public static void handleReconnection(WebDriver driver) {
        if (!isWhatsAppConnected(driver)) {
            System.out.println("WhatsApp disconnected. Attempting to reconnect...");
            try {
                driver.navigate().refresh();
                Thread.sleep(5000);

                if (!isWhatsAppConnected(driver)) {
                    System.out.println("Reconnection failed. Manual intervention required.");
                }
            } catch (Exception e) {
                System.err.println("Error during reconnection: " + e.getMessage());
            }
        }
    }
}