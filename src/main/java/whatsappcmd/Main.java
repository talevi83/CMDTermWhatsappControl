package whatsappcmd;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.List;


import static whatsappcmd.GlobalVariables.*;

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
        myChat.get(1).click();
    }

    public static void main(String[] args) throws Exception {
        try {
            System.out.println("OS detected: " + OS);
            driver = WebDriverInitialize.initializeWebDriver();
            navigateToMyPhoneChat();
            WhatsappListener.listenToNewMessages(driver);
        } catch (Exception e) {
            e.printStackTrace();
            driver.quit();
        }

        driver.quit();
    }
}
