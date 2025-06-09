package whatsappcmd.commands;

import whatsappcmd.security.PasswordManager;

public class SetupCommand implements Command {
    @Override
    public String execute(String[] args) {
        if (args.length < 1) {
            return "Usage: setup <password> - Set up the sudo password for remote commands\n" +
                   "       setup clear - Clear the stored password";
        }

        if (args[0].equalsIgnoreCase("clear")) {
            return PasswordManager.clearStoredPassword();
        }

        try {
            return PasswordManager.setupPassword(args[0]);
        } catch (Exception e) {
            return "Error setting up password: " + e.getMessage();
        }
    }

    @Override
    public String getDescription() {
        return "Set up or manage the sudo password for remote commands";
    }

    @Override
    public boolean isShellCommand() {
        return false;
    }
} 