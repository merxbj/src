package notwa.dal;

import notwa.common.ConnectionInfo;
import notwa.common.LoggingInterface;

public abstract class DataAccessLayer {
	protected DatabaseConnection dc;
	
	public DataAccessLayer() {
		LoggingInterface.getLogger().logWarning("Creating DataAccessLayer subclass with default constructor!");
		dc = null;
	}
	
	public DataAccessLayer(ConnectionInfo ci) {
		dc = new DatabaseConnection(ci);
	}
}
