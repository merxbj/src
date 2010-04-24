/*
 * AssignedProject
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
 * A simple extension of a <code>Project</code> that is aware of the <code>User</code> 
 * it is assigned to.
 *
 * <p>Actually the common <code>Project</code> has the similar feature as well, however,
 * it is aware of ALL <code>User</code>s that it is assigned to. This project, 
 * however, is aware of that ONE AND ONLY user from that collection, which it is
 * currently regarding to in the specific context</p>.
 *
 * <p>Please note that the underlying <code>Project</code> is UNDISTINGUISHABLE 
 * of that one present in the current <code>Context</code> which making this
 * extension only as an added value, which is good!</p>
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class AssignedProject extends Project {

    private User user;

    /**
     * The sole constructor building up the inner inherited <code>Project</code>
     * by that given through the parameter and assigning it to the given user.
     *
     * @param project The underlying <code>Project</code>.
     * @param user The <code>User</code> which this <code>Project</code> is assigned to.
     */
    public AssignedProject(Project project, User user) {
        super(project.getId());
        super.assignedUsers = project.assignedUsers;
        super.attachedBOC = project.attachedBOC;
        super.currentContext = project.currentContext;
        super.deleted = project.deleted;
        super.inserted = project.deleted;
        super.name = project.name;
        super.originalVersion = project.originalVersion;
        super.updated = project.updated;

        this.user = user;
    }

    /**
     * Gets the <code>User</code> we are assigned to in this context.
     *
     * @return The User.
     */
    public User getUser() {
        return user;
    }
}
