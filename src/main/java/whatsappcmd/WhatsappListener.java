package whatsappcmd;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import static whatsappcmd.GlobalVariables.*;

public class WhatsappListener {
    public static String listenToNewMessages (WebDriver driver) throws IOException, InterruptedException {
        // עכשיו מזריקים JS שמגדיר MutationObserver
        String jsObserver = ""
                + "var container = document.querySelector('div[data-tab=\"8\"][role=\"application\"]');"
                + "window.newMessages = [];"
                + "var observer = new MutationObserver(function(mutationsList) {"
                + "    for (var mutation of mutationsList) {"
                + "        for (var node of mutation.addedNodes) {"
                + "            if (node.nodeType === Node.ELEMENT_NODE) {"
                + "                if (node.querySelector('span.selectable-text')) {"
                + "                    window.newMessages.push(node.innerText);"
                + "                }"
                + "            }"
                + "        }"
                + "    }"
                + "});"
                + "observer.observe(container, { childList: true, subtree: true });";

        ((JavascriptExecutor) driver).executeScript(jsObserver);

        System.out.println("Start listening to whatsapp messages.");

        // Starting a loop to check every 1 second whether there is a new message or not.
        while (true) {
            try {
                Thread.sleep(1000); // wait 1 second between each iteration.
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // קוראים את המערך window.newMessages מ־JS
            @SuppressWarnings("unchecked")
            List<String> newMessages = (List<String>) ((JavascriptExecutor) driver)
                    .executeScript("var msgs = window.newMessages.slice(); window.newMessages = []; return msgs;");

            if (newMessages != null && !newMessages.isEmpty()) {
                String lastMessage = newMessages.getLast();
                if(lastMessage.contains("CMD:") && checkMessageTime(lastMessage)) {
                    lastMessage = lastMessage.split(": ")[1].split("\\n")[0];
                    System.out.println("Trying to run: " + CMD_TERM + " " + CMD_FLAG + " " + lastMessage);
                    Process process = Runtime.getRuntime().exec(new String[]{CMD_TERM, CMD_FLAG, lastMessage});
                    // קורא את ה־output (ה־stdout)
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(process.getInputStream()));

                    String line;
                    System.out.println("---- Output of the command ----");
                    StringBuilder sb = new StringBuilder("");
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                        sb.append(line + "\n");
                    }
                    reader.close();

                    SeleniumUtils.sendResponseOnWhatsapp(driver, sb.toString());
                    // מחכה לסיום התהליך ומדפיס exit code
                    int exitCode = process.waitFor();
                    System.out.println("Exit Code: " + exitCode);
                }
            }
        }
    }

    private static boolean checkMessageTime(String lastMessage) {

        lastMessage = lastMessage.split("\n")[1];

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        // convert to LocalTime
        LocalTime inputTime = LocalTime.parse(lastMessage, formatter);
        LocalTime now = LocalTime.now();

        // Calculate time difference.
        long secondsDiff = Math.abs(now.toSecondOfDay() - inputTime.toSecondOfDay());

        // Check if the time difference is higher than 60 seconds.
        return secondsDiff < 60;
    }
}
