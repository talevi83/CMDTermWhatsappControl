package whatsappcmd;

import whatsappcmd.commands.Command;

public class CommandExecutor {
    protected static CommandResult processMessage(String message) {
        String[] parts = message.trim().split("\\s+");
        String commandName = parts[0];
        String[] args = new String[parts.length - 1];
        System.arraycopy(parts, 1, args, 0, args.length);

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
