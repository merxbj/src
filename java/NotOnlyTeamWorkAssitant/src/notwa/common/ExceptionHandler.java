package notwa.common;

import notwa.logger.LogDispatcher;
import notwa.logger.Logger;

public class ExceptionHandler {
	
	private static ExceptionHandler singleton;
	private LogDispatcher ld;
	
	public static ExceptionHandler getInstanece() {
		if (singleton == null) {
			singleton = new ExceptionHandler();
		}
		return singleton;
	}
	
	protected ExceptionHandler() {
		ld = new LogDispatcher();
		ld.registerLogger(new Logger("./log/notwa.log", "NOTWA"));
	}
	
	public void handleException(Exception ex) {
		ld.logError(ex.toString());
	}
}
