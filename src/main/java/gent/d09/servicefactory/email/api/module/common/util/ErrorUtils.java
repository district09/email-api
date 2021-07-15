package gent.d09.servicefactory.email.api.module.common.util;

public class ErrorUtils {
    private ErrorUtils(){}

    public static boolean containsStackTrace(String message){
        if(message == null){
            return false;
        }
        return message.matches("(^\\d+\\) .+)|(^.+Exception: .+)|(^\\s+at .+)|(^\\s+... \\d+ more)|(^\\s*Caused by:.+)");
    }
}