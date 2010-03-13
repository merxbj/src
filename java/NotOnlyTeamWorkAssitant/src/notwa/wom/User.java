package notwa.wom;

public class User extends BusinessObject implements Comparable<User>, Cloneable {

    private int userID;
    private String login;
    private String password;
    private String firstName;
    private String lastName;
    private ProjectCollection assignedProjects;
    
    public User(int userID) {
        this.userID = userID;
    }
    
    protected Object clone() throws CloneNotSupportedException {
        User clone = (User) super.clone();

        clone.userID = this.userID;
        clone.login = this.login;
        clone.password = this.password;
        clone.firstName = this.firstName;
        clone.lastName = this.lastName;
        clone.assignedProjects = this.assignedProjects;
        return clone;
    }
    
    @Override
    public String toString() {
        String returnText = new String(    this.userID +separator+
                                        this.login +separator+
                                        this.firstName +separator+
                                        this.lastName );
        return returnText;
    }
    
    public String getLoginName() {
        return this.login;
    }

    public String getLoginPass() {
        return this.password;
    }
    
    public String getFirstName() {
        return this.firstName;
    }
    
    public String getLastName() {
        return this.lastName;
    }
    
    public ProjectCollection getAssignedProjects() {
        return assignedProjects;
    }
    
    public void setLoginName(String loginName) {
        this.login = loginName;
    }
    
    public void setUserPassword(String userPassword) {
        this.password = userPassword;
    }
    
    public void setUserFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public void setAssignedProjects(ProjectCollection assignedProjects) {
        this.assignedProjects = assignedProjects;
    }
    
    @Override
    public int compareTo(User user) {
        Integer j1 = this.userID;
        Integer j2 = user.userID;
     
        return j1.compareTo(j2);
    }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User)) {
            return false;
        } else {
            return (this.compareTo((User) o) == 0);
        }
    }
}
