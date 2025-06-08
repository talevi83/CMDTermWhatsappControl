package whatsappcmd;

import whatsappcmd.commands.Command;

import java.util.HashMap;
import java.util.Map;

public class CommandRegistry {
    private static final Map<String, Command> commands = new HashMap<>();

    public static void registerCommand(String name, Command command) {
        commands.put(name.toLowerCase(), command);
    }

    public static Command getCommand(String name) {
        return commands.get(name.toLowerCase());
    }

    public static Map<String, Command> getAllCommands() {
        return commands;
    }
}
