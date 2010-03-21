package notwa.wom;

import java.util.Hashtable;

public class Context {

    private int contextId;
    private Hashtable<Integer, WorkItem> workItemMap;
    private Hashtable<NotePrimaryKey, Note> noteMap;
    private Hashtable<Integer, Project> projectMap;
    private Hashtable<Integer, User> userMap;

    public Context(int contextId) {
        this.contextId = contextId;
        this.noteMap = new Hashtable<NotePrimaryKey, Note>();
        this.projectMap = new Hashtable<Integer, Project>();
        this.userMap = new Hashtable<Integer, User>();
        this.workItemMap = new Hashtable<Integer, WorkItem>();
    }

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

    public void clear() {
        noteMap.clear();
        projectMap.clear();
        workItemMap.clear();
        userMap.clear();
    }

    public boolean hasUser(int userId) {
        return userMap.containsKey(userId);
    }

    public boolean hasProject(int projectId) {
        return projectMap.containsKey(projectId);
    }

    public boolean hasWorkItem(int workItemId) {
        return workItemMap.containsKey(workItemId);
    }

    public boolean hasNote(NotePrimaryKey notePrimaryKey) {
        return noteMap.containsKey(notePrimaryKey);
    }

    public void registerUser(User user) {
        userMap.put(user.getId(), user);
    }

    public void registerProject(Project project) {
        projectMap.put(project.getId(), project);
    }

    public void registerWorkItem(WorkItem workItem) {
        workItemMap.put(workItem.getId(), workItem);
    }

    public void registerNote(Note note) {
        noteMap.put(note.getId(), note);
    }

    public WorkItem getWorkItem(int workItemId) {
        return workItemMap.get(workItemId);
    }

    public Note getNote(NotePrimaryKey notePrimaryKey) {
        return noteMap.get(notePrimaryKey);
    }

    public Project getProject(int projectId) {
        return projectMap.get(projectId);
    }

    public User getUser(int userId) {
        return userMap.get(userId);
    }
}
