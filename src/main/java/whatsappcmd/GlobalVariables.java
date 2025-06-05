package whatsappcmd;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public abstract class GlobalVariables {

    protected static Properties properties;
    static {
        try {
            properties = new Properties();
            FileInputStream input = new FileInputStream("src/main/resources/config.properties");
            properties.load(input);
            input.close();

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected static final String OS = System.getProperty("os.name");
    protected static final String HOME_DIRECTORY = System.getProperty("user.home");
    protected static final String CHROME_PROFILE_PATH = OS.toLowerCase().contains("windows") ?
            HOME_DIRECTORY + properties.getProperty("chrome.profile.windows.path") :
            HOME_DIRECTORY + properties.getProperty("chrome.profile.mac.path");

    protected static String CMD_TERM = OS.toLowerCase().contains("windows") ? "cmd.exe" : "/bin/sh";
    protected static String CMD_FLAG = OS.toLowerCase().contains("windows") ? "/c" : "-c";


}
