package notwa.common;

import notwa.logger.LogDispatcher;
import notwa.logger.Logger;

public class LoggingInterface {
    
    private static LoggingInterface singleton;
    private LogDispatcher ld;
    
    public static LoggingInterface getInstanece() {
        if (singleton == null) {
            singleton = new LoggingInterface();
        }
        return singleton;
    }
    
    protected LoggingInterface() {
        ld = new LogDispatcher();
        ld.registerLogger(new Logger("./log/notwa.log"));
    }
    
    public void handleException(Exception ex) {
        StringBuilder sb = new StringBuilder();
        sb.append(ex.toString());
        sb.append("\n");
        for (StackTraceElement ste : ex.getStackTrace()) {
            sb.append("\t at ");
            sb.append(ste.toString());
            sb.append("\n");
        }
        ld.logError(sb.toString());
    }
    
    public static LogDispatcher getLogger() {
        return getInstanece().ld;
    }
}
