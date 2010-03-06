package notwa.dal;

import notwa.common.ConnectionInfo;
import notwa.common.ExceptionHandler;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;

public class DatabaseConnection {
	private Connection con;
	private boolean connected;
	
	public DatabaseConnection(ConnectionInfo ci) {
		try {
			con = DriverManager.getConnection(ci.compileConnectionString());
			connected = true;
		} catch (Exception ex) {
			ExceptionHandler.getInstanece().handleException(ex);
			connected = false;
		}
	}
	
	public ResultSet executeQuery(String query) throws Exception {
		Statement s = con.createStatement();
		ResultSet rs = s.executeQuery(query);
		return rs;
	}

	public boolean isConnected() {
		return this.connected;
	}
}
