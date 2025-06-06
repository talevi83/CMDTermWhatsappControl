package whatsappcmd;

public class CommandInfo {
    protected final String command;
    protected final String description;

    protected CommandInfo(String command, String description) {
        this.command = command;
        this.description = description;
    }
}
