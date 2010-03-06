package notwa.common;

import java.lang.StringBuilder;

public class ConnectionInfo implements Comparable<ConnectionInfo> {
	private String label;
	private String host;
	private String port;
	private String dbname;
	private String user;
	private String password;
	
	@Override
	public int compareTo(ConnectionInfo ci) {
		int compare = host.compareTo(ci.host);
		if (compare == 0) {
			compare = port.compareTo(ci.port);
		}
		if (compare == 0) {
			compare = dbname.compareTo(ci.dbname);
		}
		if (compare == 0) {
			compare = user.compareTo(ci.user);
		}
		return compare;
	}
	@Override
	
	public boolean equals(Object obj) {
		if (!(obj instanceof ConnectionInfo)) { 
			return false;
		} else {
			return this.compareTo((ConnectionInfo) obj) == 0 ;
		}
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public void setHost(String host) {
		this.host = host;
	}
	
	public void setPort(String port) {
		this.port = port;
	}
	
	public void setDbname(String dbname) {
		this.dbname = dbname;
	}
	
	public void setUser(String user) {
		this.user = user;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	/*
	 * TODO: Implement connection string compiler!
	 */
	public String compileConnectionString() {
		StringBuilder sb = new StringBuilder();
		sb.append("jdbc:mysql:");
		sb.append("//");
		sb.append(this.host);
		sb.append(":");
		sb.append(this.port);
		sb.append("/");
		sb.append(this.dbname);
		return "jdbc:mysql://213.192.44.108:9970/notwa";
	}
	
}
