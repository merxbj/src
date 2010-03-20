package notwa.dal;

import notwa.common.ConnectionInfo;
import notwa.common.LoggingInterface;

public abstract class DataAccessLayer {
    protected static DatabaseConnection dc;
    
    public DataAccessLayer() {
        LoggingInterface.getLogger().logWarning("Creating DataAccessLayer subclass with default constructor!");
        dc = null;
    }
    
    public DataAccessLayer(ConnectionInfo ci) {
        if (dc == null) {
            dc = new DatabaseConnection(ci);
        }
    }
}
