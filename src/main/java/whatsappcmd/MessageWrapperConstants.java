package whatsappcmd;

public class MessageWrapperConstants {
    protected static String checkMessageForWrapper(String msg) {
        switch (msg) {
            case "external ip":
                return "curl https://api.ipify.org";
            case "internal ip":
                return GlobalVariables.OS.toLowerCase().contains("windows") ? "for /f \"tokens=14 delims= \" %a in ('ipconfig ^| findstr \"IPv4\"') do @echo %a\n" : "ipconfig getifaddr en0";
            case "sleep":
                return "rundll32.exe powrprof.dll,SetSuspendState 0,1,0";
            case "shutdown":
                return GlobalVariables.OS.toLowerCase().contains("windows") ? "shutdown /s /f /t 60" : "echo '" + GlobalVariables.properties.get("mac.password") + "' | sudo -S shutdown -h now";
            case "cancel shutdown":
                return "shutdown /a";
        }
        return msg;
    }
}
