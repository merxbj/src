/*
 * Context
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

import java.util.Hashtable;

/**
 * <code>Context</code> is a class representing its literal meaning and bringing
 * its advantages into the object model synchronizaction with the database.
 * <p>The main idea is to know already existing objects that have been created
 * in the same context. Whenever you are then creating an another object, you
 * can verify, whether the same object has not been already created under the same
 * context. You can then reject the creation process and reuse the same object
 * from the <code>Context</code>.</p>
 * <p>Objects created in the context are uniquely identified by their primary key,
 * which usually is their id or it could be generally a set of more properties.
 * Their storage and referencing is supplied by set of HashTables mapping id to
 * the actual object.</p>
 * <p></code>Context<code> itself is identified by its contextId and its management
 * is dedicated to the {@link ContextManager}.</p>
 *
 * @author jmerxbauer
 */
public class Context {

    private int contextId;
    private Hashtable<Integer, WorkItem> workItemMap;
    private Hashtable<NotePrimaryKey, Note> noteMap;
    private Hashtable<Integer, Project> projectMap;
    private Hashtable<Integer, User> userMap;

    /**
     * The sole constructor which initializes all known <code>Hashtable</code>s.
     *
     * @param contextId The actual id which identifies this <code>Context</code>.
     */
    public Context(int contextId) {
        this.contextId = contextId;
        this.noteMap = new Hashtable<NotePrimaryKey, Note>();
        this.projectMap = new Hashtable<Integer, Project>();
        this.userMap = new Hashtable<Integer, User>();
        this.workItemMap = new Hashtable<Integer, WorkItem>();
    }

    /**
     * The <code>Context</code> is identified by its <code>contextId</code>
     * which makes it distinguishable from other <code>Context</code>s.
     * @param obj - <code>Context</code> to be compared with.
     * @return  <code>true</code> if this <code>Context</code> eqauls to the given
     *          one, <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Context other = (Context) obj;
        if (this.contextId != other.contextId) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + this.contextId;
        return hash;
    }

    /**
     * Clears the context by clearing all of its <code>Hashtables</code> holding
     * references to <code>BusinessObject</code>s managed within this <code>Context</code>.
     */
    public void clearAll() {
        noteMap.clear();
        projectMap.clear();
        workItemMap.clear();
        userMap.clear();
    }

    /**
     * Clears the context by clearing <code>Hashtable</code> holding
     * references to <code>Note</code>s managed within this <code>Context</code>.
     */
    public void clearNotes() {
        noteMap.clear();
    }

    /**
     * Clears the context by clearing <code>Hashtable</code> holding
     * references to <code>Project</code>s managed within this <code>Context</code>.
     */
    public void clearProjects() {
        projectMap.clear();
    }

    /**
     * Clears the context by clearing <code>Hashtable</code> holding
     * references to <code>WorkItem</code>s managed within this <code>Context</code>.
     */
    public void clearWorkItems() {
        workItemMap.clear();
    }

    /**
     * Clears the context by clearing <code>Hashtable</code> holding
     * references to <code>User</code>s managed within this <code>Context</code>.
     */
    public void clearUsers() {
        userMap.clear();
    }

    /**
     * Checks whether given <code>User</code> has been ever managed within this
     * <code>Context</code>.
     * 
     * @param userId    Uniqe identifier of the <code>User</code> we are looking for
     *                  in this <code>Context</code>
     * @return  <code>true</code> if the queried <code>User</code> is maintained within
     *          this <code>Context</code>, <code>false</code> oterwise.
     */
    public boolean hasUser(int userId) {
        return userMap.containsKey(userId);
    }

    /**
     * Checks whether given <code>Project</code> has been ever managed within this
     * <code>Context</code>.
     * 
     * @param projectId Uniqe identifier of the <code>Project</code> we are looking for
     *                  in this <code>Context</code>
     * @return  <code>true</code> if the queried <code>Project</code> is maintained within
     *          this <code>Context</code>, <code>false</code> oterwise.
     */
    public boolean hasProject(int projectId) {
        return projectMap.containsKey(projectId);
    }

    /**
     * Checks whether given <code>WorkItem</code> has been ever managed within this
     * <code>Context</code>.
     * 
     * @param workItemId    Uniqe identifier of the <code>WorkItem</code> we are looking for
     *                      in this <code>Context</code>
     * @return  <code>true</code> if the queried <code>WorkItem</code> is maintained within
     *          this <code>Context</code>, <code>false</code> oterwise.
     */
    public boolean hasWorkItem(int workItemId) {
        return workItemMap.containsKey(workItemId);
    }

    /**
     * Checks whether given <code>Note</code> has been ever managed within this
     * <code>Context</code>.
     * 
     * @param notePrimaryKey    Uniqe identifier of the <code>Note</code> we are
     *                          looking for in this <code>Context</code>
     * @return  <code>true</code> if the queried <code>Note</code> is maintained within
     *          this <code>Context</code>, <code>false</code> oterwise.
     */
    public boolean hasNote(NotePrimaryKey notePrimaryKey) {
        return noteMap.containsKey(notePrimaryKey);
    }

    /**
     * Stores the given <code>User</code> into the appropriate <code>Hashtable</code> managed
     * within this <code>Context</code>. This method knows how to acquire the
     * userId from the given <code>User</code> reference.
     * 
     * @param user  <code>User</code> to be maintained by this <code>Context</code>.
     */
    public void registerUser(User user) {
        userMap.put(user.getId(), user);
    }

    /**
     * Stores the given <code>Project</code> into the appropriate <code>Hashtable</code> managed
     * within this <code>Context</code>. This method knows how to acquire the
     * projectId from the given <code>Project</code> reference.
     * 
     * @param project  <code>Project</code> to be maintained by this <code>Context</code>.
     */
    public void registerProject(Project project) {
        projectMap.put(project.getId(), project);
    }

    /**
     * Stores the given <code>WorkItem</code> into the appropriate <code>Hashtable</code> managed
     * within this <code>Context</code>. This method knows how to acquire the
     * workItemId from the given <code>WorkItem</code> reference.
     * 
     * @param workItem  <code>WorkItem</code> to be maintained by this <code>Context</code>.
     */
    public void registerWorkItem(WorkItem workItem) {
        workItemMap.put(workItem.getId(), workItem);
    }

    /**
     * Stores the given <code>Note</code> into the appropriate <code>Hashtable</code> managed
     * within this <code>Context</code>. This method knows how to acquire the
     * noteId from the given <code>Note</code> reference.
     * 
     * @param note  <code>Note</code> to be maintained by this <code>Context</code>.
     */
    public void registerNote(Note note) {
        noteMap.put(note.getId(), note);
    }

    /**
     * Gets the <code>WorkItem</code> identified by the given workItemId from the 
     * appropriate <code>Hashtable</code> managed within this <code>Context</code>.
     * 
     * @param workItemId    Uniqe identifier for the requested <code>WorkItem</code> 
     *                      which is maintained by this <code>Context</code>.
     * @return WorkItem <code>WorkItem</code> reference acquired by the given workItemId,
     *                  <code>null</code> if there is no such <code>WorkItem</code>.
     */
    public WorkItem getWorkItem(int workItemId) {
        return workItemMap.get(workItemId);
    }

    /**
     * Gets the <code>Note</code> identified by the given noteId from the
     * appropriate <code>Hashtable</code> managed within this <code>Context</code>.
     * 
     * @param noteId    Uniqe identifier for the requested <code>Note</code>
     *                  which is maintained by this <code>Context</code>.
     * @return Note <code>Note</code> reference acquired by the given noteId,
     *              <code>null</code> if there is no such <code>Note</code>.
     */
    public Note getNote(NotePrimaryKey noteId) {
        return noteMap.get(noteId);
    }

    /**
     * Gets the <code>Project</code> identified by the given projectId from the
     * appropriate <code>Hashtable</code> managed within this <code>Context</code>.
     *
     * @param projectId    Uniqe identifier for the requested <code>Project</code>
     *                  which is maintained by this <code>Context</code>.
     * @return Project  <code>Project</code> reference acquired by the given projectId,
     *                  <code>null</code> if there is no such <code>Project</code>.
     */
    public Project getProject(int projectId) {
        return projectMap.get(projectId);
    }

    /**
     * Gets the <code>User</code> identified by the given userId from the
     * appropriate <code>Hashtable</code> managed within this <code>Context</code>.
     * 
     * @param userId    Uniqe identifier for the requested <code>User</code>
     *                  which is maintained by this <code>Context</code>.
     * @return User <code>User</code> reference acquired by the given userId,
     *              <code>null</code> if there is no such <code>User</code>.
     */
    public User getUser(int userId) {
        return userMap.get(userId);
    }
}
