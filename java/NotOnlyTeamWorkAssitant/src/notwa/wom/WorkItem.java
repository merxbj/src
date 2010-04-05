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

public class WorkItem extends BusinessObject implements Comparable<WorkItem>, Cloneable {

    private int witID;
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

    public WorkItem(int id) {
        super();
        this.witID = id;
    }
    
    @Override
    protected Object clone() throws CloneNotSupportedException {
        WorkItem clone = (WorkItem) super.clone();

        clone.witID = this.witID;
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
    
    public int getId() {
        return this.witID;
    }
    
    public String getSubject() {
        return this.subject;
    }

    public WorkItemPriority getPriority() {
        return this.priority;
    }

    public String getDescription() {
        return this.description;
    }

    public WorkItemStatus getStatus() {
        return this.status;
    }

    public Date getLastModifiedTimestamp() {
        return this.lastModifiedTimestamp;
    }

    public Project getProject() {
        return this.project;
    }

    public WorkItem getParent() {
        return this.parentWorkItem;
    }

    public Date getExpectedTimestamp() {
        return this.expectedTimestamp;
    }
    
    public User getAssignedUser() {
        return assignedUser;
    }
    
    public NoteCollection getNoteCollection() {
        return noteCollection;
    }

    public void setSubject(String subject) {
        this.subject = subject;
        if (isAttached()) {
            attachedBOC.setUpdated(this);
        }
    }

    public void setPriority(WorkItemPriority priority) {
        this.priority = priority;
        if (isAttached()) {
            attachedBOC.setUpdated(this);
        }
    }

    public void setDescription(String description) {
        this.description = description;
        if (isAttached()) {
            attachedBOC.setUpdated(this);
        }
    }

    public void setAssignedUser(User assignedUser) {
        this.assignedUser = assignedUser;
        if (isAttached()) {
            attachedBOC.setUpdated(this);
        }
    }

    public void setStatus(WorkItemStatus status) {
        this.status = status;
        if (isAttached()) {
            attachedBOC.setUpdated(this);
        }
    }

    public void setLastModifiedTimestamp(Date lastModifiedTimestamp) {
        this.lastModifiedTimestamp = lastModifiedTimestamp;
        if (isAttached()) {
            attachedBOC.setUpdated(this);
        }
    }

    public void setProject(Project project) {
        this.project = project;
        if (isAttached()) {
            attachedBOC.setUpdated(this);
        }
    }

    public void setParentWorkItem(WorkItem parentWorkItem) {
        this.parentWorkItem = parentWorkItem;
        if (isAttached()) {
            attachedBOC.setUpdated(this);
        }
    }

    public void setExpectedTimestamp(Date expectedTimestamp) {
        this.expectedTimestamp = expectedTimestamp;
        if (isAttached()) {
            attachedBOC.setUpdated(this);
        }
    }
    public void setNoteCollection(NoteCollection noteCollection) {
        this.noteCollection = noteCollection;
        if (isAttached()) {
            attachedBOC.setUpdated(this);
        }
    }

    @Override
    public String toString() {
        String returnText = new String(    this.witID +separator);
        if (this.parentWorkItem != null) {
            returnText += this.parentWorkItem.getSubject() +separator; }
        if (this.project != null) {
            returnText += this.project.getName() +separator; }
        
            returnText += this.subject +separator;
            
        if (this.status != null) {
            returnText += this.status.name() +separator; }
        if (this.assignedUser != null) {
            returnText += this.assignedUser.getLogin() +separator; }
        if (this.priority != null) {
            returnText += this.priority.name() +separator; }
    
            returnText +=     this.description +separator+
                            this.lastModifiedTimestamp +separator+
                            this.expectedTimestamp;
        return returnText;
    }
    
    @Override
    public int compareTo(WorkItem wi) {
        Integer j1 = this.witID;
        Integer j2 = wi.witID;
     
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
        hash = 37 * hash + this.witID;
        return hash;
    }

    @Override
    public void registerWithContext(Context currentContext) {
        this.currentContext = currentContext;
        currentContext.registerWorkItem(this);
    }
}
