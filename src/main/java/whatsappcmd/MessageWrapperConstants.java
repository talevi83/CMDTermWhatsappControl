package whatsappcmd;

public class MessageWrapperConstants {
    protected static String checkMessageForWrapper(String msg) {
        switch (msg) {
            case "external ip":
                return "curl https://api.ipify.org";
            case "internal ip":
                return GlobalVariables.OS.toLowerCase().contains("windows") ? "for /f \"tokens=14 delims= \" %a in ('ipconfig ^| findstr \"IPv4\"') do @echo %a\n" : "ipconfig getifaddr en0";
        }
        return msg;
    }
}
