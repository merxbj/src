/*
 * CloningTest
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
package notwa.test;

import notwa.wom.*;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class CloningTest {

    /**
     * 
     */
    public CloningTest() {
        fillWithSomeData();
    }

    private void fillWithSomeData() {
        User user = new User(1);
        user.setLogin("mrneo");
        
        Project project = new Project(1);
        project.setProjectName("notwa");
        
        Note note = new Note(1,1);
        note.setAuthor(user);
        note.setNoteText("kurva jedna zasrana");
        
        NoteCollection noteCollection = new NoteCollection();
        //noteCollection.add(note);
        
        
        WorkItem wi = new WorkItem(1);
        wi.setSubject("class CloningTest");
        wi.setAssignedUser(user);
        wi.setDescription("create new class CloningTest for testing this code...");
        wi.setProject(project);
        wi.setNoteCollection(noteCollection);
        
        WorkItemCollection wic = new WorkItemCollection();
        //wic.add(wi);
    }
}
