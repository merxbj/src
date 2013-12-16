/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.merxbj.toggl;

import java.util.Objects;

/**
 *
 * @author jm185267
 */
public class Task implements Comparable<Task> {

    private String user;
    private String client;
    private String project;
    private String task;
    
    public Task(String user, String client, String project, String task) {
        this.user = user;
        this.client = client;
        this.project = project;
        this.task = task;
    }

    public String getUser() {
        return user;
    }

    public String getClient() {
        return client;
    }

    public String getProject() {
        return project;
    }

    public String getTask() {
        return task;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.user);
        hash = 79 * hash + Objects.hashCode(this.client);
        hash = 79 * hash + Objects.hashCode(this.project);
        hash = 79 * hash + Objects.hashCode(this.task);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Task other = (Task) obj;
        if (!Objects.equals(this.user, other.user)) {
            return false;
        }
        if (!Objects.equals(this.client, other.client)) {
            return false;
        }
        if (!Objects.equals(this.project, other.project)) {
            return false;
        }
        if (!Objects.equals(this.task, other.task)) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(Task o) {
        if (o == null) {
            return 1;
        }
        
        int compare = this.user.compareTo(o.user);
        if (compare == 0) {
            compare = this.client.compareTo(o.client);
        }
        if (compare == 0) {
            compare = this.project.compareTo(o.project);
        }
        if (compare == 0) {
            compare = this.task.compareTo(o.task);
        }
        
        return compare;
    }

    @Override
    public String toString() {
        return client + " - " + project + " - " + task;
    }
}
