import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

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

        // מתחילים לולאה בצד Java כדי לבדוק מדי פעם אם יש newMessages
        while (true) {
            try {
                Thread.sleep(1000); // מחכה שניה בין בדיקות
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // קוראים את המערך window.newMessages מ־JS
            @SuppressWarnings("unchecked")
            List<String> newMessages = (List<String>) ((JavascriptExecutor) driver)
                    .executeScript("var msgs = window.newMessages.slice(); window.newMessages = []; return msgs;");

            if (newMessages != null && !newMessages.isEmpty()) {
                String lastMessage = newMessages.getLast();
                if(lastMessage.contains("CMD:")) {
                    lastMessage = lastMessage.split(": ")[1].split("\\n")[0];
                    Process process = Runtime.getRuntime().exec(lastMessage);
                    // קורא את ה־output (ה־stdout)
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(process.getInputStream()));

                    String line;
                    System.out.println("---- Output of the command ----");
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }
                    reader.close();

                    // מחכה לסיום התהליך ומדפיס exit code
                    int exitCode = process.waitFor();
                    System.out.println("Exit Code: " + exitCode);
                }
            }
        }
    }
}
