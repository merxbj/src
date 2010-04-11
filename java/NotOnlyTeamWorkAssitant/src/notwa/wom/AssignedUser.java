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
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class AssignedUser extends User {

    private Project project;

    /**
     * 
     * @param user
     * @param project
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
     * 
     * @return
     */
    public Project getAssignedProject() {
        return project;
    }
}
