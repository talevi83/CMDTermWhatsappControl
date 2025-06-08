package whatsappcmd;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import whatsappcmd.commands.Command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
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
                    }

                    boolean isRestrictedMode = Boolean.parseBoolean(properties.get("restricted.commands").toString());
                    String userInput = lastMessage;

                    Command command = CommandExecutor.processMessage(userInput);

                    if (command != null) {
                        // פקודה מוכרת (כל מצב - תמיד עובד)
                        String commandOutput = command.execute(new String[0]);

                        if (command.isShellCommand()) {
                            System.out.println("Trying to run: " + CMD_TERM + " " + CMD_FLAG + " " + commandOutput);
                            Process process = Runtime.getRuntime().exec(new String[]{CMD_TERM, CMD_FLAG, commandOutput});

                            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                            String line;
                            StringBuilder sb = new StringBuilder("");
                            while ((line = reader.readLine()) != null) {
                                System.out.println(line);
                                sb.append(line).append("\n");
                            }
                            reader.close();

                            SeleniumUtils.sendResponseOnWhatsapp(driver, sb.toString());
                            int exitCode = process.waitFor();
                            System.out.println("Exit Code: " + exitCode);

                            if (commandOutput.contains("rundll32.exe powrprof.dll,SetSuspendState")) {
                                String msg = "Closing program before sleep...";
                                SeleniumUtils.sendResponseOnWhatsapp(driver, msg);
                                System.out.println(msg);
                                driver.quit();
                                Thread.sleep(2);
                            }
                        } else {
                            // פקודה שמחזירה output (למשל help)
                            SeleniumUtils.sendResponseOnWhatsapp(driver, commandOutput);
                        }
                    } else {
                        if (isRestrictedMode) {
                            SeleniumUtils.sendResponseOnWhatsapp(driver, "Restricted commands is enabled. Only predefined commands are allowed.");
                        } else {
                            System.out.println("Trying to run: " + CMD_TERM + " " + CMD_FLAG + " " + userInput);
                            Process process = Runtime.getRuntime().exec(new String[]{CMD_TERM, CMD_FLAG, userInput});

                            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                            String line;
                            StringBuilder sb = new StringBuilder("");
                            while ((line = reader.readLine()) != null) {
                                System.out.println(line);
                                sb.append(line).append("\n");
                            }
                            reader.close();

                            SeleniumUtils.sendResponseOnWhatsapp(driver, sb.toString());
                            int exitCode = process.waitFor();
                            System.out.println("Exit Code: " + exitCode);
                        }
                    }
                }
            }
        }
        if(!keepListening) {
            System.out.println("Program is closing now...");
            SeleniumUtils.sendResponseOnWhatsapp(driver, "Program is closing... Goodbye!");
            Thread.sleep(Duration.ofSeconds(1));
            driver.quit();
        }
    }

    private static void sendManualAtStart() throws InterruptedException {
        String commandsHelp = getAvailableCommandsHelp();
        SeleniumUtils.sendResponseOnWhatsapp(driver, commandsHelp);
        if(Boolean.parseBoolean(properties.get("restricted.commands").toString())){
            SeleniumUtils.sendResponseOnWhatsapp(driver, "**Please Note! Restricted commands is Enabled!*");
        }
    }

    private static String getAvailableCommandsHelp() {
        StringBuilder sb = new StringBuilder("*Available Commands:*\n");
        CommandRegistry.getAllCommands().forEach((name, cmd) -> {
            sb.append("*- ").append(name).append(":* ").append(cmd.getDescription()).append("\n");
        });
        return sb.toString();
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
