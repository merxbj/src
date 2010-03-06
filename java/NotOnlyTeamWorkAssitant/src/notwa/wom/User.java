package notwa.wom;

public class User {

	private int uID;
	private String uLogin;
	private String uPassword;
	private String uUserFirstName;
	private String uUserLastName;
	
	public User(Object[] user) {
		parseDataRow(user);
	}

	private void parseDataRow(Object[] user) {
		this.uID = ((Integer) user[0]).intValue();
		this.uLogin = user[1].toString();
		this.uPassword = user[2].toString();
		this.uUserFirstName = user[3].toString();
		this.uUserLastName = user[4].toString();		
	}
	
	public int getUserID() {
		return this.uID;
	}
	
	public String getLoginName() {
		return this.uLogin;
	}
	
	public String getFirstName() {
		return this.uUserFirstName;
	}
	
	public String getLastName() {
		return this.uUserLastName;
	}
	
	public boolean verifyPassword() {
		//check password somewhere, idkfa where and how
		return false;
	}
}
