package whatsappcmd.commands;

import whatsappcmd.security.PasswordManager;
import whatsappcmd.SeleniumUtils;

import java.io.*;

import static whatsappcmd.GlobalVariables.*;

public class SleepCommand implements Command {
    @Override
    public String execute(String[] args) {
        if(OS.toLowerCase().contains("windows")) {
            try {
                SeleniumUtils.sendResponseOnWhatsapp(driver, "Putting computer to sleep in 3 seconds...");
                Thread.sleep(3000); // Give user time to see the message
            } catch (InterruptedException e) {
                return "Error sending response: " + e.getMessage();
            }
            return "rundll32.exe powrprof.dll,SetSuspendState 0,1,0";
        } else if(OS.toLowerCase().contains("mac")) {
            try {
                SeleniumUtils.sendResponseOnWhatsapp(driver, "Putting computer to sleep in 3 seconds...");
                Thread.sleep(3000); // Give user time to see the message
                
                // Create a temporary script file
                File scriptFile = File.createTempFile("sudo_script", ".sh");
                scriptFile.setExecutable(true);
                
                // Write the sudo command to the script
                try (FileWriter writer = new FileWriter(scriptFile)) {
                    writer.write("#!/bin/sh\n");
                    writer.write("echo '" + PasswordManager.getPassword() + "' | sudo -S pmset sleepnow\n");
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
            // Linux
            try {
                SeleniumUtils.sendResponseOnWhatsapp(driver, "Putting computer to sleep in 3 seconds...");
                Thread.sleep(3000); // Give user time to see the message
            } catch (InterruptedException e) {
                return "Error sending response: " + e.getMessage();
            }
            return "systemctl suspend";
        }
    }

    @Override
    public String getDescription() {
        return "Put the computer to sleep";
    }

    @Override
    public boolean isShellCommand() {
        // Only return true for Windows and Linux, as we handle Mac execution internally
        return !OS.toLowerCase().contains("mac");
    }
}
