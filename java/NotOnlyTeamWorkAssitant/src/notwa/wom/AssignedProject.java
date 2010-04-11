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
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class AssignedProject extends Project {

    private User user;

    /**
     * 
     * @param project
     * @param user
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
     * 
     * @return
     */
    public User getAssignedUser() {
        return user;
    }
}
