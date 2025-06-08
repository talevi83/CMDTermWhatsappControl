package whatsappcmd.commands;

public interface Command {
    String execute(String[] args);
    String getDescription();
}
