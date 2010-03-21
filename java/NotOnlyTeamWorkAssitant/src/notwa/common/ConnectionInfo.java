package notwa.common;

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

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.host != null ? this.host.hashCode() : 0);
        hash = 53 * hash + (this.port != null ? this.port.hashCode() : 0);
        hash = 53 * hash + (this.dbname != null ? this.dbname.hashCode() : 0);
        return hash;
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
    
    public String getLabel() {
        return this.label;
    }

    public String getPassword() {
        return password;
    }

    public String getUser() {
        return user;
    }

    public String compileConnectionString() {
        String cs = String.format("jdbc:mysql://%s:%s/%s?zeroDateTimeBehavior=convertToNull",
                this.host,
                this.port,
                this.dbname);
        return cs;
    }
    
}
