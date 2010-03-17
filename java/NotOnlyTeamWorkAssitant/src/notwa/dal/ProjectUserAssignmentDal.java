package notwa.dal;

import notwa.wom.User;
import notwa.wom.UserCollection;
import notwa.wom.ProjectCollection;
import notwa.wom.Project;
import notwa.common.ConnectionInfo;
import notwa.common.LoggingInterface;

import java.sql.ResultSet;
import java.util.Hashtable;

public class ProjectUserAssignmentDal extends DataAccessLayer {

    private Hashtable<Integer, User> users;
    private Hashtable<Integer, Project> projects;

    public ProjectUserAssignmentDal(ConnectionInfo ci) {
        super(ci);
        users = new Hashtable<Integer, User>();
        projects = new Hashtable<Integer, Project>();
    }

    public void link(UserCollection uc, ProjectCollection pc) {
        for (User u : uc) {
            users.put(u.getId(), u);
        }

        for (Project p : pc) {
            projects.put(p.getId(), p);
        }

        StringBuilder vanillaSql = new StringBuilder();

        vanillaSql.append("SELECT   user_id, ");
        vanillaSql.append("         project_id, ");
        vanillaSql.append("FROM Project_User_Assigment");

        linkProjectsAndUsers(vanillaSql.toString());
    }

    private void linkProjectsAndUsers(String sql) {
        try {
            ResultSet rs = dc.executeQuery(sql);
            while (rs.next()) {
                Project p = projects.get(rs.getInt("project_id"));
                User u = users.get(rs.getInt("user_id"));
                if (p != null && u != null) {
                    p.addAssignedUser(u);
                    u.addAssignedProject(p);
                }
            }
        } catch (Exception ex) {
            LoggingInterface.getInstanece().handleException(ex);
        }
    }

}
