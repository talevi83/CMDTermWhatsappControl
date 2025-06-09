package whatsappcmd.commands;

import whatsappcmd.GlobalVariables;
import whatsappcmd.security.PasswordManager;

import java.io.*;
import java.util.Arrays;

import static whatsappcmd.GlobalVariables.*;

public class SleepCommand implements Command {
    @Override
    public String execute(String[] args) {
        if(OS.toLowerCase().contains("windows")) {
            return "rundll32.exe powrprof.dll,SetSuspendState 0,1,0";
        } else if(OS.toLowerCase().contains("mac")) {
            try {
                // Create a temporary script file
                File scriptFile = File.createTempFile("sudo_script", ".sh");
                scriptFile.setExecutable(true);
                
                // Write the sudo command to the script
                try (FileWriter writer = new FileWriter(scriptFile)) {
                    writer.write("#!/bin/sh\n");
                    writer.write("echo '" + PasswordManager.getPassword() + "' | sudo -S shutdown -s now\n");
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
                    return "Sleep command executed successfully";
                } else {
                    return "Failed to execute sleep command. Exit code: " + exitCode + "\nOutput: " + output.toString();
                }
            } catch (Exception e) {
                return "Error executing sleep command: " + e.getMessage();
            }
        } else {
            return "systemctl suspend";
        }
    }

    @Override
    public String getDescription() {
        return "Put the computer to sleep";
    }

    @Override
    public boolean isShellCommand() {
        return false; // Changed to false since we're handling the command execution ourselves
    }
}
