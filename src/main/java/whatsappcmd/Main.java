package whatsappcmd;

import whatsappcmd.commands.*;

import static whatsappcmd.GlobalVariables.*;

public class Main {

    public static void main(String[] args) {
        try {
            registerCommands();
            System.out.println("OS detected: " + OS);
            WebDriverInitialize.initializeWebDriver();
            SeleniumUtils.navigateToMyPhoneChat();
            WhatsappListener.listenToNewMessages(driver);
        } catch (Exception e) {
            e.printStackTrace();
            driver.quit();
        }

        driver.quit();
    }

    private static void registerCommands() {
        CommandRegistry.registerCommand("externalip", new ExternalIpCommand());
        CommandRegistry.registerCommand("internalip", new InternalIpCommand());
        CommandRegistry.registerCommand("sleep", new SleepCommand());
        CommandRegistry.registerCommand("shutdown", new ShutdownCommand());
        CommandRegistry.registerCommand("cancelshutdown", new CancelShutdownCommand());
        CommandRegistry.registerCommand("help", new HelpCommand());
    }
}
