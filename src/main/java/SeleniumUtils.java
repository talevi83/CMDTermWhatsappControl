import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;


public class SeleniumUtils {
    protected static WebElement waitForElement(WebDriver driver, By locator, int timeoutInSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        System.out.println("Element is visible: " + element.getText());
        return element;
    }

    protected static List<WebElement> waitForElements(WebDriver driver, By locator, int timeoutInSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        return driver.findElements(locator);
    }

    protected static void waitForWhatsAppToLoad(WebDriver driver) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

            // Wait for either QR code (need to scan) or chat interface (already logged in)
            try {
                // Check if we need to scan QR code
                wait.until(ExpectedConditions.or(
                        ExpectedConditions.presenceOfElementLocated(By.xpath("//canvas[@role='img']")), // QR code
                        ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@data-testid='chat-list']")), // Chat list
                        ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class, 'landing-window')]")) // Landing page
                ));

                // Additional wait to ensure page is fully loaded
                Thread.sleep(3000);

                System.out.println("WhatsApp Web loaded successfully");

            } catch (Exception e) {
                System.out.println("Timeout waiting for WhatsApp to load. Manual intervention may be required.");
            }

        } catch (Exception e) {
            System.err.println("Error waiting for WhatsApp to load: " + e.getMessage());
        }
    }
}
