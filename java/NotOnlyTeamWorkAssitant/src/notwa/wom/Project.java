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
    protected int projectId;
    protected String name;
    protected UserCollection assignedUsers;

    public Project() {
        super();
        this.projectId = 0;
    }
    
    public Project(int projectId) {
        super();
        this.projectId = projectId;
    }
    
    @Override
    protected Object clone() throws CloneNotSupportedException {
        Project clone = (Project) super.clone();

        clone.projectId = this.projectId;
        clone.name = this.name;
        clone.assignedUsers = this.assignedUsers;
        return clone;
    }

    public int getId() {
        return this.projectId;
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

    public void setProjectId(int projectId) {
        if (!isAttached()) {
            this.projectId = projectId;
        }
    }
    
    @Override
    public String toString() {
        String returnText = new String (    this.projectId +separator+
                                            this.name);
        return returnText;
    }
        
    @Override
    public int compareTo(Project project) {
        Integer j1 = this.projectId;
        Integer j2 = project.projectId;
     
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
        hash = 89 * hash + this.projectId;
        return hash;
    }

    @Override
    public void registerWithContext(Context currentContext) {
        this.currentContext = currentContext;
        currentContext.registerProject(this);
    }

    @Override
    public boolean hasUniqeIdentifier() {
        return (this.projectId > 0);
    }

    @Override
    public void setUniqeIdentifier(int value) {
        this.projectId = value;
    }

    @Override
    public int getUniqeIdentifier() {
        return getId();
    }
}
