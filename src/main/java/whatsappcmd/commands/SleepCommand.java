package whatsappcmd.commands;

import whatsappcmd.GlobalVariables;

import static whatsappcmd.GlobalVariables.*;

public class SleepCommand implements Command {
    @Override
    public String execute(String[] args) {
        if(OS.toLowerCase().contains("windows")) {
            return "rundll32.exe powrprof.dll,SetSuspendState 0,1,0";
        } else if(OS.toLowerCase().contains("mac")) {
            return "echo '" + GlobalVariables.properties.get("mac.password") + "' | sudo shutdown -s now";
        } else {
            return "systemctl suspend";
        }
    }

    @Override
    public String getDescription() {
        return "Put the computer to sleep";
    }

    @Override
    public boolean isShellCommand() {
        return true;
    }
}
