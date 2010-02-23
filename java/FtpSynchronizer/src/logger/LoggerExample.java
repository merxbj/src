package logger;

public class LoggerExample {

    public static void main(String[] args) {
        log = new LogDispatcher();
        log.registerLogger(new Logger(".\\first.log", "MainModule"));
        log.registerLogger(new Logger(".\\second.log", "StillMainModule", new LogExceptionHandlerImpl()));

        log.logError("What the fuck!");
        log.logInfo("There have %d errors occured!", 666);
        //log.logWarning(null);
    }

    private static LogDispatcher log;

    private static class LogExceptionHandlerImpl implements LoggingExceptionHandler{

        public void loggingExceptionOccured(Exception ex) {
            System.out.println(ex.toString());
        }

    }
}
