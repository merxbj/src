/*
 * Project
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

public class Project extends BusinessObject implements Comparable<Project>, Cloneable {
    protected int projectID;
    protected String name;
    protected UserCollection assignedUsers;
    
    public Project(int projectID) {
        super();
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
        if (isAttached()) {
            attachedBOC.setUpdated(this);
        }
    }
    
    public void setAssignedUsers(UserCollection assignedUsers) throws ContextException {
        this.assignedUsers = new UserCollection(currentContext, assignedUsers.getResultSet());
        for (User u : assignedUsers) {
            this.assignedUsers.add(new AssignedUser(u, this));
        }

        this.assignedUsers.setClosed(assignedUsers.isClosed());
        this.assignedUsers.setUpdateRequired(assignedUsers.isUpdateRequired());

        if (isAttached()) {
            attachedBOC.setUpdated(this);
        }
    }

    public boolean addAssignedUser(User assignedUser) throws ContextException {
        return this.assignedUsers.add(new AssignedUser(assignedUser, this));
    }

    public boolean removeAssignedUser(User assignedUser) throws ContextException {
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

    @Override
    public void registerWithContext(Context currentContext) {
        this.currentContext = currentContext;
        currentContext.registerProject(this);
    }

}
