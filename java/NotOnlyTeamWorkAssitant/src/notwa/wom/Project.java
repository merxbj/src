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

/**
 * <code>Project</code> represents a single project maintainable under this application.
 *
 * @author Jaroslav Merxbauer
 * @author Tomas Studnicka
 */
public class Project extends BusinessObject implements Comparable<Project>, Cloneable {

    /**
     * The uniqe identifier of this <code>Project</code>.
     */
    protected int id;
    
    /**
     * The name of this <code>Project</code>.
     */
    protected String name;

    /**
     * The collection of <code>User</code>s assigned to this <code>Project</code>.
     */
    protected UserCollection assignedUsers;

    /**
     * The simple construtor expecting that the uniqe identifier will be supplied
     * later.
     */
    public Project() {
        super();
        this.id = 0;
    }
    
    /**
     * Full feature constructor.
     *
     * @param id The id of this <code>Project</code>.
     */
    public Project(int id) {
        super();
        this.id = id;
    }
    
    @Override
    protected Object clone() throws CloneNotSupportedException {
        Project clone = (Project) super.clone();

        clone.id = this.id;
        clone.name = this.name;
        clone.assignedUsers = this.assignedUsers;
        return clone;
    }

    /**
     * Gets this <code>Project</code> uniqe identifier.
     *
     * @return The <code>Project</code> uniqe identifier.
     */
    public int getId() {
        return this.id;
    }

    /**
     * Gets this project name.
     *
     * @return The project name.
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * Gets the <code>UserCollection</code> of<code>User</code>s assigned to this
     * <code>Project</code>.
     * This collection should contain instances of {@link AssignedUsers}.
     *
     * @return The <code>AssignedUser</code> collection.
     */
    public UserCollection getAssignedUsers() {
        return assignedUsers;
    }
    
    /**
     * Sets this <code>Project</code> name.
     *
     * <p>Please consider the consequences when changing this property when
     * this <code>Project</code> is already a member of a closed
     * <code>BusinessObjectCollection</code>.</p>
     *
     * @param projectName The new project name.
     */
    public void setProjectName(String projectName) {
        this.name = projectName;
        if (isAttached()) {
            attachedBOC.setUpdated(this);
        }
    }
    
    /**
     * Sets a new collection of <code>AssignedUser</code>s.
     * 
     * @param assignedUsers The new collection of <code>AssignedUser</code>s.
     * @throws ContextException In case this <code>Project</code>s context do not
     *                          match to the <code>AssignedUser</code>s collection.
     */
    public void setAssignedUsers(UserCollection assignedUsers) throws ContextException {
        this.assignedUsers = new UserCollection(currentContext, assignedUsers.getResultSet());
        for (User u : assignedUsers) {
            this.assignedUsers.add(new AssignedUser(u, this));
        }
    }

    /**
     * Adds the given <code>User</code> transfomed to a <code>AssignedUser</code>
     * to the assignedUsers collection.
     *
     * @param assignedUser The <code>AssignedUser</code>.
     * @return <code>true</code> if the addition succeeded, <code>false</code> otherwise.
     * @throws ContextException If the given <code>User</code> doesn't live in the
     *                          same <code>Context</code> as its collection.
     */
    public boolean addAssignedUser(User assignedUser) throws ContextException {
        if (assignedUsers == null) {
            return false;
        }

        return this.assignedUsers.add(new AssignedUser(assignedUser, this));
    }

    /**
     * Removes the given <code>User</code> from the assignedUsers collection.
     *
     * @param assignedUser The <code>User</code> to be removed.
     * @return <code>true</code> if the removal succeeded, <code>false</code> otherwise.
     * @throws ContextException If the given <code>User</code> doesn't live in the
     *                          same <code>Context</code> as its collection.
     */
    public boolean removeAssignedUser(User assignedUser) throws ContextException {
        if (assignedUsers == null) {
            return false;
        }

        return this.assignedUsers.remove(assignedUser);
    }
    
    @Override
    public String toString() {
        return String.format("%d | %s", id, name);
    }
        
    @Override
    public int compareTo(Project project) {
        Integer j1 = this.id;
        Integer j2 = project.id;
     
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
        hash = 89 * hash + this.id;
        return hash;
    }

    @Override
    public void registerWithContext(Context currentContext) {
        this.currentContext = currentContext;
        currentContext.registerProject(this);
    }

    @Override
    public boolean hasUniqeIdentifier() {
        return (this.id > 0);
    }

    @Override
    public void setUniqeIdentifier(int value) {
        this.id = value;
    }

    @Override
    public int getUniqeIdentifier() {
        return getId();
    }
}
