/*
 * User
 *
 * Copyright (C) 2010  Jaroslav Merxbauer
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package notwa.wom;

import notwa.exception.ContextException;

public class User extends BusinessObject implements Comparable<User>, Cloneable {

    protected int userID;
    protected String login;
    protected String password;
    protected String firstName;
    protected String lastName;
    protected ProjectCollection assignedProjects;
    
    public User(int userID) {
        super();
        this.userID = userID;
    }
    
    @Override
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

    public int getId() {
        return userID;
    }

    public String getLogin() {
        return this.login;
    }

    public String getPassword() {
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
    
    public void setLogin(String loginName) {
        this.login = loginName;
        if (isAttached()) {
            attachedBOC.setUpdated(this);
        }
    }
    
    public void setPassword(String userPassword) {
        this.password = userPassword;
        if (isAttached()) {
            attachedBOC.setUpdated(this);
        }
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
        if (isAttached()) {
            attachedBOC.setUpdated(this);
        }
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
        if (isAttached()) {
            attachedBOC.setUpdated(this);
        }
    }
    
    public void setAssignedProjects(ProjectCollection assignedProjects) throws ContextException {
        this.assignedProjects = new ProjectCollection(currentContext, assignedProjects.getResultSet());
        for (Project p : assignedProjects) {
            this.assignedProjects.add(new AssignedProject(p, this));
        }

        this.assignedProjects.setClosed(assignedProjects.isClosed());
        this.assignedProjects.setUpdateRequired(assignedProjects.isUpdateRequired());

        if (isAttached()) {
            attachedBOC.setUpdated(this);
        }
    }

    public boolean addAssignedProject(Project assignedProject) throws ContextException {
        return this.assignedProjects.add(new AssignedProject(assignedProject, this));
    }

    public boolean removeAssignedProject(Project assignedProject) throws ContextException {
        return this.assignedProjects.remove(assignedProject);
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

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + this.userID;
        return hash;
    }

    @Override
    public void registerWithContext(Context currentContext) {
        this.currentContext = currentContext;
        currentContext.registerUser(this);
    }

}
