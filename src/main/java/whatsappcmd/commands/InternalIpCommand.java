package whatsappcmd.commands;
import static whatsappcmd.GlobalVariables.*;

public class InternalIpCommand implements Command {
    @Override
    public String execute(String[] args) {
        if (OS.toLowerCase().contains("windows")) {
            return "for /f \"tokens=14 delims= \" %a in ('ipconfig ^| findstr \"IPv4\"') do @echo %a";
        } else {
            return "ipconfig getifaddr en0";
        }
    }

    @Override
    public String getDescription() {
        return "Get internal IP address";
    }

    @Override
    public boolean isShellCommand() {
        return true;
    }
}
