package notwa.wom;

public class User extends BusinessObject {

	private int uID;
	private String uLogin;
	private String uPassword;
	private String uUserFirstName;
	private String uUserLastName;
	
	public User(int userID) {
		this.uID = userID;
	}

	public int getUserID() {
		return this.uID;
	}
	
	public String getLoginName() {
		return this.uLogin;
	}

	public String getLoginPass() {
		return this.uPassword;
	}
	
	public String getFirstName() {
		return this.uUserFirstName;
	}
	
	public String getLastName() {
		return this.uUserLastName;
	}
	
	public void setLoginName(String loginName) {
		this.uLogin = loginName;
	}
	
	public void setUserPassword(String userPassword) {
		this.uPassword = userPassword;
	}
	
	public void setUserFirstName(String firstName) {
		this.uUserFirstName = firstName;
	}
	
	public void setLastName(String lastName) {
		this.uUserLastName = lastName;
	}
}
