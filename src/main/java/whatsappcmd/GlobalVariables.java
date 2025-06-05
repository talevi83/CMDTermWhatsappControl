package whatsappcmd;

import org.openqa.selenium.WebDriver;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public abstract class GlobalVariables {

    protected static Properties properties;
    static {
        try {
            properties = new Properties();

            // load the config file in UTF-8 encoding.
            InputStream inputStream = GlobalVariables.class.getClassLoader().getResourceAsStream("config.properties");
            if (inputStream == null) {
                throw new FileNotFoundException("Property file 'config.properties' not found in the classpath");
            }

            InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            properties.load(reader);

            reader.close();
            inputStream.close();

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

    protected static WebDriver driver;
}
