package whatsappcmd;

import whatsappcmd.commands.Command;

public class CommandExecutor {
    protected static String processMessage(String message) {
        String[] parts = message.trim().split("\\s+");
        String commandName = parts[0];
        String[] args = new String[parts.length - 1];
        System.arraycopy(parts, 1, args, 0, args.length);

        Command command = CommandRegistry.getCommand(commandName);
        if (command != null) {
            return command.execute(args);
        } else {
            return null;
        }
    }
}
