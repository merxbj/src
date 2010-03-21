package notwa.dal;

import notwa.common.ConnectionInfo;
import notwa.common.LoggingInterface;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;

public class DatabaseConnection {
    private Connection con;
    private ConnectionInfo ci;
    
    public DatabaseConnection(ConnectionInfo ci) {
        this.ci = ci;
    }
    
    public ResultSet executeQuery(String query) throws Exception {
        if (!isConnected()) {
            connect();
        }
        Statement s = con.createStatement();
        ResultSet rs = s.executeQuery(query);
        return rs;
    }

    public boolean isConnected() {
        try {
            if (con != null) {
                return !this.con.isClosed();
            } else {
                return false;
            }
        } catch (Exception ex) {
            LoggingInterface.getInstanece().handleException(ex);
            return false;
        }
    }

    public void close() {
        if (isConnected()) {
            try {
                con.close();
            } catch (Exception ex) {
                LoggingInterface.getInstanece().handleException(ex);
            }
        }
    }

    private void connect() throws Exception {
        try {
            con = DriverManager.getConnection(ci.compileConnectionString(), ci.getUser(), ci.getPassword());
        } catch (Exception ex) {
            LoggingInterface.getInstanece().handleException(ex);
            throw ex;
        }
    }
}
