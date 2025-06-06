package whatsappcmd;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static whatsappcmd.GlobalVariables.*;


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

    protected static void sendResponseOnWhatsapp(WebDriver driver, String msg) {
        List<WebElement> msgTextBox = driver.findElements(By.cssSelector("div[contenteditable='true'][data-tab='10']"));
        if (!msgTextBox.isEmpty()) {
            msgTextBox.getFirst().sendKeys(msg.replaceAll("\t", "").replaceAll("\\n", Keys.chord(Keys.ALT, Keys.ENTER)) + Keys.ENTER);
        }
    }

    protected static void navigateToMyPhoneChat() throws InterruptedException {
        WebElement searchTextBox = SeleniumUtils.waitForElement(driver,  By.cssSelector("div[role='textbox']"), 10);
        searchTextBox.click();
        Thread.sleep(Duration.ofSeconds(1));
        searchTextBox.sendKeys(CONTACT);
        Thread.sleep(Duration.ofSeconds(1));
        List<WebElement> myChat = driver.findElements(
                By.xpath("//span[contains(text(), '" + CONTACT + "')]"));
        myChat.get(1).click();
    }
}
