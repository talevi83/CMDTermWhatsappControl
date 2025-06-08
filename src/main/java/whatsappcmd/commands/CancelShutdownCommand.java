package whatsappcmd.commands;

public class CancelShutdownCommand implements Command {
    @Override
    public String execute(String[] args) {
        return "shutdown /a";
    }

    @Override
    public String getDescription() {
        return "Cancel a pending shutdown (Windows only)";
    }

    @Override
    public boolean isShellCommand() {
        return true;
    }
}
