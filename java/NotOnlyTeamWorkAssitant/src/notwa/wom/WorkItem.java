/*
 * WorkItem
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

import java.util.Date;

/**
 * <code>WorkItem</code> represents a single work item which is the main object
 * of interested of this application.
 *
 * @author Jaroslav Merxbauer
 * @author Tomas Studnicka
 */
public class WorkItem extends BusinessObject implements Comparable<WorkItem>, Cloneable {

    private int id;
    private String subject;
    private WorkItemPriority priority;
    private String description;
    private User assignedUser;
    private WorkItemStatus status;
    private Date lastModifiedTimestamp;
    private Project project;
    private WorkItem parentWorkItem;
    private Date expectedTimestamp;
    private NoteCollection noteCollection;

    /**
     * The simple construtor expecting that the uniqe identifier will be supplied
     * later.
     */
    public WorkItem() {
        super();
        this.id = 0;
    }

    /**
     * Full feature constructor.
     *
     * @param id The id of this <code>WorkItem</code>.
     */
    public WorkItem(int id) {
        super();
        this.id = id;
    }
    
    @Override
    protected Object clone() throws CloneNotSupportedException {
        WorkItem clone = (WorkItem) super.clone();

        clone.id = this.id;
        clone.subject = this.subject;
        clone.priority = this.priority;
        clone.description = this.description;
        clone.assignedUser = this.assignedUser;
        clone.status = this.status;
        clone.lastModifiedTimestamp = this.lastModifiedTimestamp;
        clone.project = this.project;
        clone.parentWorkItem = this.parentWorkItem;
        clone.expectedTimestamp = this.expectedTimestamp;
        clone.noteCollection = this.noteCollection;
        return clone;
    }
    
    /**
     * Gets this <code>WorkItem</code> uniqe identifier.
     *
     * @return The <code>WorkItem</code> uniqe identifier.
     */
    public int getId() {
        return this.id;
    }
    
    /**
     * Gets this <code>WorkItem</code> subject.
     *
     * @return The <code>WorkItem</code> subject.
     */
    public String getSubject() {
        return this.subject;
    }

    /**
     * Gets this <code>WorkItem</code> priority.
     *
     * @return The <code>WorkItem</code> priority.
     */
    public WorkItemPriority getPriority() {
        return this.priority;
    }

    /**
     * Gets this <code>WorkItem</code> description.
     *
     * @return The <code>WorkItem</code> description.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Gets this <code>WorkItem</code> status.
     *
     * @return The <code>WorkItem</code> status.
     */
    public WorkItemStatus getStatus() {
        return this.status;
    }

    /**
     * Gets this <code>WorkItem</code> last modified timestamp.
     *
     * @return The <code>WorkItem</code> last modified timestamp.
     */
    public Date getLastModifiedTimestamp() {
        return this.lastModifiedTimestamp;
    }

    /**
     * Gets the <code>Project</code> this <code>WorkItem</code> belongs to.
     *
     * @return The <code>Project</code>.
     */
    public Project getProject() {
        return this.project;
    }

    /**
     * Gets this <code>WorkItem</code> parent <code>WorkItem</code>.
     *
     * @return The parent <code>WorkItem</code>.
     */
    public WorkItem getParent() {
        return this.parentWorkItem;
    }

    /**
     * Gets this <code>WorkItem</code> expected timestamp (deadline).
     *
     * @return The expected timestamp.
     */
    public Date getExpectedTimestamp() {
        return this.expectedTimestamp;
    }
    
    /**
     * Gets the <code>User</code> currently assigned to this <code>WorkItem</code>.
     *
     * @return The assigned <code>User</code>.
     */
    public User getAssignedUser() {
        return assignedUser;
    }
    
    /**
     * Gets the <code>NoteCollection</code> which have been written regarding this
     * <code>WorkItem</code>.
     *
     * @return The <code>NoteCollection</code>.
     */
    public NoteCollection getNoteCollection() {
        return noteCollection;
    }

    /**
     * Sets this <code>WorkItem</code> subject.
     *
     * <p>Please consider the consequences when changing this property when
     * this <code>Project</code> is already a member of a closed
     * <code>BusinessObjectCollection</code>.</p>
     *
     * @param subject The <code>WorkItem</code> subject.
     */
    public void setSubject(String subject) {
        this.subject = subject;
        if (isAttached()) {
            attachedBOC.setUpdated(this);
        }
    }

    /**
     * Sets this <code>WorkItem</code> priority.
     *
     * <p>Please consider the consequences when changing this property when
     * this <code>Project</code> is already a member of a closed
     * <code>BusinessObjectCollection</code>.</p>
     *
     * @param priority The <code>WorkItem</code> priority.
     */
    public void setPriority(WorkItemPriority priority) {
        this.priority = priority;
        if (isAttached()) {
            attachedBOC.setUpdated(this);
        }
    }

    /**
     * Sets this <code>WorkItem</code> description.
     *
     * <p>Please consider the consequences when changing this property when
     * this <code>Project</code> is already a member of a closed
     * <code>BusinessObjectCollection</code>.</p>
     *
     * @param description The <code>WorkItem</code> description.
     */
    public void setDescription(String description) {
        this.description = description;
        if (isAttached()) {
            attachedBOC.setUpdated(this);
        }
    }

    /**
     * Sets this <code>WorkItem</code> assigned user.
     *
     * <p>Please consider the consequences when changing this property when
     * this <code>Project</code> is already a member of a closed
     * <code>BusinessObjectCollection</code>.</p>
     *
     * @param assignedUser The <code>WorkItem</code> assigned user.
     */
    public void setAssignedUser(User assignedUser) {
        this.assignedUser = assignedUser;
        if (isAttached()) {
            attachedBOC.setUpdated(this);
        }
    }

    /**
     * Sets this <code>WorkItem</code> status.
     *
     * <p>Please consider the consequences when changing this property when
     * this <code>Project</code> is already a member of a closed
     * <code>BusinessObjectCollection</code>.</p>
     *
     * @param status The <code>WorkItem</code> status.
     */
    public void setStatus(WorkItemStatus status) {
        this.status = status;
        if (isAttached()) {
            attachedBOC.setUpdated(this);
        }
    }

    /**
     * Sets this <code>WorkItem</code> last modified timestamp.
     *
     * <p>Please consider the consequences when changing this property when
     * this <code>Project</code> is already a member of a closed
     * <code>BusinessObjectCollection</code>.</p>
     *
     * @param lastModifiedTimestamp The <code>WorkItem</code> last modified timestamp.
     */
    public void setLastModifiedTimestamp(Date lastModifiedTimestamp) {
        this.lastModifiedTimestamp = lastModifiedTimestamp;
        if (isAttached()) {
            attachedBOC.setUpdated(this);
        }
    }

    /**
     * Sets this <code>WorkItem</code> project.
     *
     * <p>Please consider the consequences when changing this property when
     * this <code>Project</code> is already a member of a closed
     * <code>BusinessObjectCollection</code>.</p>
     *
     * @param project The <code>WorkItem</code> project.
     */
    public void setProject(Project project) {
        this.project = project;
        if (isAttached()) {
            attachedBOC.setUpdated(this);
        }
    }

    /**
     * Sets this <code>WorkItem</code> parent <code>WorkItem</code>.
     *
     * <p>Please consider the consequences when changing this property when
     * this <code>Project</code> is already a member of a closed
     * <code>BusinessObjectCollection</code>.</p>
     *
     * @param parentWorkItem The <code>WorkItem</code> parent <code>WorkItem</code>.
     */
    public void setParentWorkItem(WorkItem parentWorkItem) {
        this.parentWorkItem = parentWorkItem;
        if (isAttached()) {
            attachedBOC.setUpdated(this);
        }
    }

    /**
     * Sets this <code>WorkItem</code> expected timestamp.
     *
     * <p>Please consider the consequences when changing this property when
     * this <code>Project</code> is already a member of a closed
     * <code>BusinessObjectCollection</code>.</p>
     *
     * @param expectedTimestamp The <code>WorkItem</code> expected timestamp.
     */
    public void setExpectedTimestamp(Date expectedTimestamp) {
        this.expectedTimestamp = expectedTimestamp;
        if (isAttached()) {
            attachedBOC.setUpdated(this);
        }
    }

    /**
     * Sets this <code>WorkItem</code> note collection.
     *
     * @param noteCollection The <code>WorkItem</code> colleciton of  <code>Note</code>.
     */
    public void setNoteCollection(NoteCollection noteCollection) {
        this.noteCollection = noteCollection;
    }

    @Override
    public String toString() {
        String returnText = String.format("%d | %s | %s | %s | %s | %s | %s | %s | %s | %s", 
                id,
                (parentWorkItem != null) ? parentWorkItem.getSubject() : "pwi:null",
                (project != null) ? project.getName() : "proj:null",
                subject,
                (status != null) ? status.name() : "s:null",
                (assignedUser != null) ? assignedUser.getLogin() : "u:null",
                (priority != null) ? priority.name() : "prij:null",
                description,
                lastModifiedTimestamp,
                expectedTimestamp
                );
        
        return returnText;
    }
    
    @Override
    public int compareTo(WorkItem wi) {
        Integer j1 = this.id;
        Integer j2 = wi.id;
     
        return j1.compareTo(j2);
    }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof WorkItem)) {
            return false;
        } else {
            return (this.compareTo((WorkItem) o) == 0);
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + this.id;
        return hash;
    }

    @Override
    public void registerWithContext(Context currentContext) {
        this.currentContext = currentContext;
        currentContext.registerWorkItem(this);
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
