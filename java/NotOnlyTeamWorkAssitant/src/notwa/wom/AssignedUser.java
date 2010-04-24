/*
 * AssignedUser
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

/**
 * A simple extension of a <code>User</code> that is aware of the <code>Project</code>
 * it is assigned to.
 *
 * <p>Actually the common <code>User</code> has the similar feature as well, however,
 * it is aware of ALL <code>Project</code>s that it is assigned to. This project, 
 * however, is aware of that ONE AND ONLY user from that collection, which it is
 * currently regarding to in the specific context</p>.
 *
 * <p>Please note that the underlying <code>User</code> is UNDISTINGUISHABLE of 
 * that one present in the current <code>Context</code> which making this extension
 * only as an added value, which is good!</p>
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class AssignedUser extends User {

    private Project project;

    /**
     * The sole constructor building up the inner inherited <code>Project</code>
     * by that given through the parameter and assigning it to the given user.
     *
     * @param user The underlying <code>User</code>.
     * @param project The <code>Project</code> which this <code>User</code> is assigned to.
     */
    public AssignedUser(User user, Project project) {
        super(user.getId());
        super.assignedProjects = user.assignedProjects;
        super.firstName = user.firstName;
        super.lastName = user.lastName;
        super.login = user.login;
        super.password = user.password;
        super.attachedBOC = user.attachedBOC;
        super.currentContext = user.currentContext;
        super.deleted = user.deleted;
        super.inserted = user.inserted;
        super.originalVersion = user.originalVersion;
        super.updated = user.updated;
        
        this.project = project;
    }

    /**
     * Gets the <code>Project</code> we are assigned to in this context.
     *
     * @return The Project.
     */
    public Project getProject() {
        return project;
    }
}
