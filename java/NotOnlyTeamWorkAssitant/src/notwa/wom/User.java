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
import notwa.security.Credentials;

/**
 * <code>User</code> represents a single user using this application.
 *
 * @author Jaroslav Merxbauer
 * @author Tomas Studnicka
 */
public class User extends BusinessObject implements Comparable<User>, Cloneable {

    /**
     * The uniqe identifier of this <code>User</code>.
     */
    protected int userId;

    /**
     * The login of this <code>User</code>.
     */
    protected String login;

    /**
     * The secret password of this <code>User</code>.
     */
    protected String password;

    /**
     * The first name of this <code>User</code>.
     */
    protected String firstName;
    
    /**
     * The last name of this <code>User</code>.
     */
    protected String lastName;

    /**
     * The collection of <code>Projects</code>s assigned to this <code>User</code>.
     */
    protected ProjectCollection assignedProjects;

    /**
     * The simple construtor expecting that the uniqe identifier will be supplied
     * later.
     */
    public User() {
        super();
        this.userId = 0;
    }

    /**
     * Full feature constructor.
     *
     * @param id The id of this <code>User</code>.
     */
    public User(int id) {
        super();
        this.userId = id;
    }
    
    @Override
    protected Object clone() throws CloneNotSupportedException {
        User clone = (User) super.clone();

        clone.userId = this.userId;
        clone.login = this.login;
        clone.password = this.password;
        clone.firstName = this.firstName;
        clone.lastName = this.lastName;
        clone.assignedProjects = this.assignedProjects;
        return clone;
    }
    
    @Override
    public String toString() {
        return String.format("%d | %s | %s | %s", userId, login, firstName, lastName);
    }

    /**
     * Gets this <code>User</code> uniqe identifier.
     *
     * @return The <code>User</code> uniqe identifier.
     */
    public int getId() {
        return userId;
    }

    /**
     * Gets this <code>User</code> login. This property is then used as a part
     * of {@link Credentials} to be validated together with password againts
     * the requested database.
     *
     * @return The <code>User</code> login string.
     */
    public String getLogin() {
        return this.login;
    }

    /**
     * Gets this <code>User</code> password. This property is then used as a part
     * of {@link Credentials} to be validated together with login againts
     * the requested database.
     *
     * @return The <code>User</code> password string.
     */
    public String getPassword() {
        return this.password;
    }
    
    /**
     * Gets this <code>User</code> first name.
     *
     * @return The <code>User</code> first name.
     */
    public String getFirstName() {
        return this.firstName;
    }
    
    /**
     * Gets this <code>User</code> last name.
     *
     * @return The <code>User</code> last name.
     */
    public String getLastName() {
        return this.lastName;
    }
    
    /**
     * Gets the <code>ProjectCollection</code> of <code>Project</code>s assigned 
     * to this <code>User</code>.
     * This collection should contain instances of {@link AssignedProjects}.
     *
     * @return The <code>AssignedUser</code> collection.
     */
    public ProjectCollection getAssignedProjects() {
        return assignedProjects;
    }
    
    /**
     * Sets this <code>User</code> login.
     *
     * <p>Please consider the consequences when changing this property when
     * this <code>Project</code> is already a member of a closed
     * <code>BusinessObjectCollection</code>.</p>
     *
     * @param loginName The <code>User</code> new login.
     */
    public void setLogin(String loginName) {
        this.login = loginName;
        if (isAttached()) {
            attachedBOC.setUpdated(this);
        }
    }
    
    /**
     * Sets this <code>User</code> password.
     *
     * <p>Please consider the consequences when changing this property when
     * this <code>Project</code> is already a member of a closed
     * <code>BusinessObjectCollection</code>.</p>
     *
     * @param userPassword The <code>User</code> new password.
     */
    public void setPassword(String userPassword) {
        this.password = userPassword;
        if (isAttached()) {
            attachedBOC.setUpdated(this);
        }
    }
    
    /**
     * Sets this <code>User</code> first name.
     *
     * <p>Please consider the consequences when changing this property when
     * this <code>Project</code> is already a member of a closed
     * <code>BusinessObjectCollection</code>.</p>
     *
     * @param firstName The <code>User</code> new first name.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
        if (isAttached()) {
            attachedBOC.setUpdated(this);
        }
    }
    
    /**
     * Sets this <code>User</code> last name.
     *
     * <p>Please consider the consequences when changing this property when
     * this <code>Project</code> is already a member of a closed
     * <code>BusinessObjectCollection</code>.</p>
     *
     * @param lastName The <code>User</code> new lastName.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
        if (isAttached()) {
            attachedBOC.setUpdated(this);
        }
    }
    
    /**
     * Sets a new collection of <code>AssignedPorject</code>s.
     * 
     * @param assignedProjects  The new collection of <code>AssignedProject</code>s.
     * @throws ContextException In case this <code>User</code>s context do not
     *                          match to the <code>AssignedProject</code>s collection.
     */
    public void setAssignedProjects(ProjectCollection assignedProjects) throws ContextException {
        this.assignedProjects = new ProjectCollection(currentContext, assignedProjects.getResultSet());
        for (Project p : assignedProjects) {
            this.assignedProjects.add(new AssignedProject(p, this));
        }
    }

    /**
     * Adds the given <code>Project</code> transfomed to a <code>AssignedProject</code>
     * to the assignedProjects collection.
     *
     * @param assignedProject The <code>AssignedProject</code>.
     * @return <code>true</code> if the addition succeeded, <code>false</code> otherwise.
     * @throws ContextException If the given <code>Project</code> doesn't live in the
     *                          same <code>Context</code> as its collection.
     */
    public boolean addAssignedProject(Project assignedProject) throws ContextException {
        return this.assignedProjects.add(new AssignedProject(assignedProject, this));
    }

    /**
     * Removes the given <code>Project</code> from the assignedProject collection.
     *
     * @param assignedProject The <code>Project</code> to be removed.
     * @return <code>true</code> if the removal succeeded, <code>false</code> otherwise.
     * @throws ContextException If the given <code>Project</code> doesn't live in the
     *                          same <code>Context</code> as its collection.
     */
    public boolean removeAssignedProject(Project assignedProject) throws ContextException {
        return this.assignedProjects.remove(assignedProject);
    }
    
    @Override
    public int compareTo(User user) {
        Integer j1 = this.userId;
        Integer j2 = user.userId;
     
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
        hash = 67 * hash + this.userId;
        return hash;
    }

    @Override
    public void registerWithContext(Context currentContext) {
        this.currentContext = currentContext;
        currentContext.registerUser(this);
    }

    @Override
    public boolean hasUniqeIdentifier() {
        return (this.userId > 0);
    }

    @Override
    public void setUniqeIdentifier(int value) {
        this.userId = value;
    }

    @Override
    public int getUniqeIdentifier() {
        return getId();
    }

}
