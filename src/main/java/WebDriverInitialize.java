import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class WebDriverInitialize {

    private static ChromeOptions options = new ChromeOptions();

    protected static WebDriver initilizeWebDriver() {
        System.out.println("Closing all chrome instances before starting webdriver...");
        closeExistingChromeInstances();

        System.out.println("Loading whatsapp chrome profile.");
        copyAndUseProfile();

        System.out.println("Opening chrome browser using Selenium.");
        WebDriver driver = new ChromeDriver(options);

        System.out.println("Navigate to web.whatsapp.com");
        driver.get("https://web.whatsapp.com");
//        SeleniumUtils.waitForElement(driver);
        return driver;
    }

    private static void copyAndUseProfile() {
        try {
            // Source profile path
            Path sourceProfile = Paths.get("/Users/tallevi/Library/Application Support/Google/Chrome/Profile 1");

            // Destination profile path
            Path destProfile = Paths.get(System.getProperty("user.home") + "/selenium_whatsapp_profile");

            // Copy profile if it doesn't exist
            if (!Files.exists(destProfile) && Files.exists(sourceProfile)) {
                System.out.println("Copying Chrome profile...");
                copyDirectory(sourceProfile, destProfile);
            }

            ChromeOptions options = new ChromeOptions();
            options.addArguments("--user-data-dir=" + System.getProperty("user.home"));
            options.addArguments("--profile-directory=selenium_whatsapp_profile");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");
            options.addArguments("--remote-debugging-port=0");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void copyDirectory(Path source, Path destination) throws IOException {
        Files.walk(source)
                .forEach(sourcePath -> {
                    try {
                        Path destPath = destination.resolve(source.relativize(sourcePath));
                        if (Files.isDirectory(sourcePath)) {
                            Files.createDirectories(destPath);
                        } else {
                            Files.copy(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    // Close any existing Chrome instances before starting Selenium
    public static void closeExistingChromeInstances() {
        try {
            if (System.getProperty("os.name").toLowerCase().contains("mac")) {
                Runtime.getRuntime().exec("pkill -f 'Google Chrome'");
            } else if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                Runtime.getRuntime().exec("taskkill /f /im chrome.exe");
            } else {
                Runtime.getRuntime().exec("pkill chrome");
            }
            Thread.sleep(2000); // Wait for processes to close
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
