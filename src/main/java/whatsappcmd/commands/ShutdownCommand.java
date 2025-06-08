package whatsappcmd.commands;

import whatsappcmd.GlobalVariables;

import static whatsappcmd.GlobalVariables.OS;

public class ShutdownCommand implements Command {
    @Override
    public String execute(String[] args) {
        if (OS.toLowerCase().contains("windows")) {
            return "shutdown /s /f /t 60";
        } else {
            return "echo '" + GlobalVariables.properties.get("mac.password") + "' | sudo -S shutdown -h now";
        }
    }

    @Override
    public String getDescription() {
        return "Shutdown the computer";
    }

    @Override
    public boolean isShellCommand() {
        return true;
    }
}
