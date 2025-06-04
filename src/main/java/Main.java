import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

public class Main {

    private static WebDriver driver;

    protected static void navigateToMyPhoneChat() throws InterruptedException {
        WebElement searchTextBox = SeleniumUtils.waitForElement(driver,  By.cssSelector("div[role='textbox']"), 10);
        searchTextBox.click();
        Thread.sleep(Duration.ofSeconds(1));
        searchTextBox.sendKeys("הטלפון שלי");
        Thread.sleep(Duration.ofSeconds(1));
        List<WebElement> myChat = driver.findElements(
                By.xpath("//span[contains(text(), 'הטלפון שלי')]"));
        myChat.get(1).click(); // אם אתה רוצה ללחוץ עליו
    }

    protected static void readLastMessage() throws IOException, InterruptedException {
        List<WebElement> lastMessage = SeleniumUtils.waitForElements(driver, By.cssSelector("div.copyable-text[data-pre-plain-text*='Tal Levi']"), 10);
//        String lastMessageTxt = lastMessage.get(lastMessage.size()-1).getText();
        WhatsappListener.listenToNewMessages(driver);
    }

    protected static void listenToChanges() {
        try {
            WhatsappListener.listenToNewMessages(driver);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws Exception {
        try {
            driver = WebDriverInitialize.initializeWebDriver();
            System.out.println();
            navigateToMyPhoneChat();
            readLastMessage();
        } catch (Exception e) {
            e.printStackTrace();
            driver.quit();
        }

        driver.quit();
    }
}
