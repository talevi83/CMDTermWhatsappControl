package whatsappcmd.commands;

import whatsappcmd.CommandRegistry;

public class HelpCommand implements Command {
    @Override
    public String execute(String[] args) {
        StringBuilder sb = new StringBuilder("Available Commands:\n");
        CommandRegistry.getAllCommands().forEach((name, cmd) -> {
            sb.append("- ").append(name).append(": ").append(cmd.getDescription()).append("\n");
        });
        return sb.toString();
    }

    @Override
    public String getDescription() {
        return "Show this help message";
    }
}
