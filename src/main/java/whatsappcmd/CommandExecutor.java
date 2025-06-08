package whatsappcmd;

import whatsappcmd.commands.Command;

public class CommandExecutor {
    protected static Command processMessage(String message) {
        String[] parts = message.trim().split("\\s+");
        String commandName = parts[0];
        String[] args = new String[parts.length - 1];
        System.arraycopy(parts, 1, args, 0, args.length);

        return CommandRegistry.getCommand(commandName);
    }
}
