package whatsappcmd;

import static whatsappcmd.GlobalVariables.*;

public class Main {

    public static void main(String[] args) {
        try {
            System.out.println("OS detected: " + OS);
            driver = WebDriverInitialize.initializeWebDriver();
            SeleniumUtils.navigateToMyPhoneChat();
            WhatsappListener.listenToNewMessages(driver);
        } catch (Exception e) {
            e.printStackTrace();
            driver.quit();
        }

        driver.quit();
    }
}
