package whatsappcmd;

import whatsappcmd.commands.Command;

public class CommandExecutor {
    protected static CommandResult processMessage(String message) {
        String[] parts = message.trim().split("\\s+");
        if (parts.length == 0) {
            return new CommandResult(null, new String[0]);
        }
        
        String commandName = parts[0];
        String[] args = parts.length > 1 ? new String[parts.length - 1] : new String[0];
        if (args.length > 0) {
            System.arraycopy(parts, 1, args, 0, args.length);
        }

        Command command = CommandRegistry.getCommand(commandName);
        return new CommandResult(command, args);
    }

    public static class CommandResult {
        private final Command command;
        private final String[] args;

        public CommandResult(Command command, String[] args) {
            this.command = command;
            this.args = args;
        }

        public Command getCommand() {
            return command;
        }

        public String[] getArgs() {
            return args;
        }
    }
}
