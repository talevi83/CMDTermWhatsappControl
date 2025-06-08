package whatsappcmd.commands;

public class ExternalIpCommand implements Command {

    @Override
    public String execute(String[] args) {
        return "curl https://api.ipify.org";
    }

    @Override
    public String getDescription() {
        return "Get external IP address";
    }

    @Override
    public boolean isShellCommand() {
        return true;
    }
}
