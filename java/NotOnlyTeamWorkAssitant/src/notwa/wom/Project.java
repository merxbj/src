package notwa.wom;

public class Project extends BusinessObject implements Comparable<Project>, Cloneable {
    private int projectID;
    private String name;
    private UserCollection assignedUsers;
    
    public Project (int projectID) {
        this.projectID = projectID;
    }
    
    @Override
    protected Object clone() throws CloneNotSupportedException {
        Project clone = (Project) super.clone();

        clone.projectID = this.projectID;
        clone.name = this.name;
        clone.assignedUsers = this.assignedUsers;
        return clone;
    }

    public int getId() {
        return this.projectID;
    }

    public String getName() {
        return this.name;
    }
    
    public UserCollection getAssignedUsers() {
        return assignedUsers;
    }
    
    public void setProjectName(String projectName) {
        this.name = projectName;
    }
    
    public void setAssignedUsers(UserCollection assignedUsers) {
        this.assignedUsers = assignedUsers;
    }

    public boolean addAssignedUser(User assignedUser) {
        return this.assignedUsers.add(assignedUser);
    }

    public boolean removeAssignedUser(User assignedUser) {
        return this.assignedUsers.remove(assignedUser);
    }
    
    @Override
    public String toString() {
        String returnText = new String (    this.projectID +separator+
                                            this.name);
        return returnText;
    }
        
    @Override
    public int compareTo(Project project) {
        Integer j1 = this.projectID;
        Integer j2 = project.projectID;
     
        return j1.compareTo(j2);
    }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Project)) {
            return false;
        } else {
            return (this.compareTo((Project) o) == 0);
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + this.projectID;
        return hash;
    }
}
