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
    public static void listenToNewMessages (WebDriver driver) throws IOException, InterruptedException {
        // Inject JS MutationObserver.
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

        boolean justStarted = true;
        boolean keepListening = true;

        // Starting a loop to check every 1 second whether there is a new message or not.
        while (keepListening) {
            try {
                Thread.sleep(1000); // wait 1 second between each iteration.
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(justStarted) {
                sendManualAtStart();
                justStarted = false;
            }

            @SuppressWarnings("unchecked")
            List<String> newMessages = (List<String>) ((JavascriptExecutor) driver)
                    .executeScript("var msgs = window.newMessages.slice(); window.newMessages = []; return msgs;");

            if (newMessages != null && !newMessages.isEmpty()) {
                String lastMessage = newMessages.getLast();
                if(lastMessage.contains("CMD:") && checkMessageTime(lastMessage)) {
                    lastMessage = lastMessage.split(": ")[1].split("\\n")[0];
                    if(lastMessage.toLowerCase().equals("close program")){
                        keepListening = false;
                        break;
                    } else if(lastMessage.toLowerCase().equals("manual")) {
                        sendManualAtStart();
                    }

                    lastMessage = MessageWrapperConstants.checkMessageForWrapper(lastMessage);
                    if(Boolean.parseBoolean(properties.get("restricted.commands").toString()) && lastMessage.startsWith("Restricted commands is enabled.")) {
                        SeleniumUtils.sendResponseOnWhatsapp(driver, lastMessage);
                    } else {
                        System.out.println("Trying to run: " + CMD_TERM + " " + CMD_FLAG + " " + lastMessage);
                        Process process = Runtime.getRuntime().exec(new String[]{CMD_TERM, CMD_FLAG, lastMessage});

                        // Read the output from the command line.
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

                        int exitCode = process.waitFor();
                        System.out.println("Exit Code: " + exitCode);
                    }
                }
            }
        }
        if(!keepListening) {
            driver.quit();
        }
    }

    private static void sendManualAtStart() {
        String commandsHelp = MessageWrapperConstants.getAvailableCommandsHelp();
        SeleniumUtils.sendResponseOnWhatsapp(driver, commandsHelp);
        if(Boolean.parseBoolean(properties.get("restricted.commands").toString())){
            SeleniumUtils.sendResponseOnWhatsapp(driver, "**Please Note! Restricted commands is Enabled!*");
        }
    }

    private static boolean checkMessageTime(String lastMessage) {
        try {
            lastMessage = lastMessage.split("\n")[1];

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

            // convert to LocalTime
            LocalTime inputTime = LocalTime.parse(lastMessage, formatter);
            LocalTime now = LocalTime.now();

            // Calculate time difference.
            long secondsDiff = Math.abs(now.toSecondOfDay() - inputTime.toSecondOfDay());

            // Check if the time difference is higher than 60 seconds.
            return secondsDiff < 60;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }

    }
}
