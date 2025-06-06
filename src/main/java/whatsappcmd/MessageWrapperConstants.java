package whatsappcmd;

import java.util.HashMap;
import java.util.Map;

public class MessageWrapperConstants {

    private static final Map<String, CommandInfo> commandMap = new HashMap<>();

    static {
        commandMap.put("external ip", new CommandInfo("curl https://api.ipify.org", "Get the external (public) IP address"));
        commandMap.put("internal ip", new CommandInfo(
                GlobalVariables.OS.toLowerCase().contains("windows") ?
                        "for /f \"tokens=14 delims= \" %a in ('ipconfig ^| findstr \"IPv4\"') do @echo %a\n"
                        :
                        "ipconfig getifaddr en0",
                "Get the internal (local) IP address"
        ));
        commandMap.put("sleep", new CommandInfo("rundll32.exe powrprof.dll,SetSuspendState 0,1,0", "Put the computer to sleep"));
        commandMap.put("shutdown", new CommandInfo(
                GlobalVariables.OS.toLowerCase().contains("windows") ?
                        "shutdown /s /f /t 60"
                        :
                        "echo '" + GlobalVariables.properties.get("mac.password") + "' | sudo -S shutdown -h now",
                "Shutdown the computer"
        ));
        commandMap.put("cancel shutdown", new CommandInfo("shutdown /a", "Cancel a pending shutdown (Windows only)"));
    }

    protected static String checkMessageForWrapper(String msg) {
        CommandInfo info = commandMap.get(msg);
        if (info != null) {
            return info.command;
        } else if(Boolean.parseBoolean(GlobalVariables.properties.get("restricted.commands").toString())) {
            return "Restricted commands is enabled.\n" +
                    "Command '" + msg + "' not found.";
        } else {
            return msg;
        }
    }

    public static String getAvailableCommandsHelp() {
        StringBuilder sb = new StringBuilder("*Available commands:*\n");
        sb.append("*To run command, the message should start with a prefix 'CMD: <command>'*\n\n");

        for (Map.Entry<String, CommandInfo> entry : commandMap.entrySet()) {
            sb.append(String.format(" %-16s : %s\n", entry.getKey(), entry.getValue().description));
        }
        sb.append(String.format(" %-16s : %s\n", "close program", "Closing the program and shutdown WebDriver."));
        sb.append(String.format(" %-16s : %s\n", "manual", "Get this manual."));
        return sb.toString();
    }
}

