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
		ld.registerLogger(new Logger("./log/notwa.log", "NOTWA"));
	}
	
	public void handleException(Exception ex) {
		ld.logError(ex.toString());
	}
	
	public static LogDispatcher getLogger() {
		return getInstanece().ld;
	}
}
