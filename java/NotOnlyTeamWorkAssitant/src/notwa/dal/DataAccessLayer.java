package notwa.dal;

import java.util.Hashtable;
import notwa.common.ConnectionInfo;
import notwa.common.LoggingInterface;
import notwa.wom.Context;

public abstract class DataAccessLayer {
    protected static Hashtable<ConnectionInfo, DatabaseConnection> connections;
    protected ConnectionInfo ci;
    protected Context currentContext;
    
    public DataAccessLayer() {
        LoggingInterface.getLogger().logWarning("Creating DataAccessLayer subclass with default constructor!");
    }
    
    public DataAccessLayer(ConnectionInfo ci, Context context) {
        this.ci = ci;
        this.currentContext = context;

        if (connections == null) {
            connections = new Hashtable<ConnectionInfo, DatabaseConnection>();
        }
        if ((ci != null) && !connections.containsKey(ci)) {
            connections.put(ci, new DatabaseConnection(ci));
        }
    }

    public DatabaseConnection getConnection() {
        return connections.get(ci);
    }
}
