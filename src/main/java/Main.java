import org.openqa.selenium.WebDriver;

public class Main {

    private static WebDriver driver;

    public static void main(String[] args) throws Exception {
        try {
            driver = WebDriverInitialize.initilizeWebDriver();

        } catch (Exception e) {
            e.printStackTrace();
            driver.quit();
        }

        driver.quit();
    }
}
