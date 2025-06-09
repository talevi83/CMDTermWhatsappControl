package whatsappcmd.commands;

import whatsappcmd.GlobalVariables;
import whatsappcmd.security.PasswordManager;

import java.io.*;
import java.util.Arrays;

import static whatsappcmd.GlobalVariables.OS;

public class ShutdownCommand implements Command {
    @Override
    public String execute(String[] args) {
        if (OS.toLowerCase().contains("windows")) {
            return "shutdown /s /f /t 60";
        } else {
            try {
                // Create a temporary script file
                File scriptFile = File.createTempFile("sudo_script", ".sh");
                scriptFile.setExecutable(true);
                
                // Write the sudo command to the script
                try (FileWriter writer = new FileWriter(scriptFile)) {
                    writer.write("#!/bin/sh\n");
                    writer.write("echo '" + PasswordManager.getPassword() + "' | sudo -S shutdown -h now\n");
                }

                // Execute the script
                ProcessBuilder pb = new ProcessBuilder("/bin/sh", scriptFile.getAbsolutePath());
                pb.redirectErrorStream(true);
                Process process = pb.start();

                // Read the output
                StringBuilder output = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        output.append(line).append("\n");
                    }
                }

                // Wait for the process to complete
                int exitCode = process.waitFor();
                
                // Clean up the temporary file
                scriptFile.delete();

                if (exitCode == 0) {
                    return "Shutdown command executed successfully";
                } else {
                    return "Failed to execute shutdown command. Exit code: " + exitCode + "\nOutput: " + output.toString();
                }
            } catch (Exception e) {
                return "Error executing shutdown command: " + e.getMessage();
            }
        }
    }

    @Override
    public String getDescription() {
        return "Shutdown the computer";
    }

    @Override
    public boolean isShellCommand() {
        return false; // Changed to false since we're handling the command execution ourselves
    }
}
