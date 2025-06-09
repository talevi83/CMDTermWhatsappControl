package whatsappcmd;

import org.openqa.selenium.WebDriver;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public abstract class GlobalVariables {

    public static Properties properties = loadPropertiesFile();

    public static final String OS = System.getProperty("os.name");
    protected static final String HOME_DIRECTORY = System.getProperty("user.home");
    protected static final String CHROME_PROFILE_PATH = OS.toLowerCase().contains("windows") ?
            HOME_DIRECTORY + properties.getProperty("chrome.profile.windows.path") :
            HOME_DIRECTORY + properties.getProperty("chrome.profile.mac.path");
    protected static String CONTACT = properties.getProperty("whatsapp.contact");

    protected static String CMD_TERM = OS.toLowerCase().contains("windows") ? "cmd.exe" : "/bin/sh";
    protected static String CMD_FLAG = OS.toLowerCase().contains("windows") ? "/c" : "-c";

    public static WebDriver driver;

    private static Properties loadPropertiesFile() {
        try {
            Properties properties = new Properties();
            InputStream inputStream;

            // Load the properties file from the Jar directory.
            File jarDir = new File(System.getProperty("java.class.path")).getAbsoluteFile().getParentFile();
            File externalFile = new File(jarDir, "config.properties");

            if (externalFile.exists()) {
                inputStream = new FileInputStream(externalFile);
                System.out.println("Loaded external config.properties");
            } else {
                // fallback: load the file from the Jar file.
                inputStream = GlobalVariables.class.getClassLoader().getResourceAsStream("config.properties");
                if (inputStream == null) {
                    throw new FileNotFoundException("Property file 'config.properties' not found externally or in the JAR");
                }
                System.out.println("Loaded internal config.properties from JAR");
            }

            // Read the file with UTF-8 encoding.
            InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            properties.load(reader);

            reader.close();
            inputStream.close();
            return properties;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }


}
