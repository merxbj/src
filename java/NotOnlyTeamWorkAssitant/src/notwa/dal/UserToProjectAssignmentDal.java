package notwa.dal;

import notwa.common.ConnectionInfo;
import notwa.sql.ParameterCollection;
import notwa.wom.ProjectCollection;
import notwa.wom.Project;
import notwa.sql.SqlBuilder;
import notwa.common.LoggingInterface;
import notwa.wom.Context;
import notwa.sql.Parameters;
import notwa.sql.Parameter;
import notwa.sql.Sql;
import notwa.exception.DalException;

import java.sql.ResultSet;

public class UserToProjectAssignmentDal extends DataAccessLayer implements Fillable<ProjectCollection> {

    private ConnectionInfo ci;
    private Context currentContext;

    public UserToProjectAssignmentDal(ConnectionInfo ci, Context context) {
        super(ci);
        this.ci = ci;
        this.currentContext = context;
    }

    @Override
    public int Fill(ProjectCollection col) {
        ParameterCollection emptyPc = new ParameterCollection();
        return Fill(col, emptyPc);
    }

    @Override
    public int Fill(ProjectCollection col, ParameterCollection pc) {
        StringBuilder vanillaSql = new StringBuilder();

        vanillaSql.append("SELECT   pua.project_id ");
        vanillaSql.append("FROM Project_User_Assignment pua ");
        vanillaSql.append("JOIN User u ");
        vanillaSql.append("ON u.user_id = pua.user_id ");
        vanillaSql.append("/** STATEMENT=WHERE;RELATION=AND;");
        vanillaSql.append("        {column=pua.user_id;parameter=UserId;}");
        vanillaSql.append("**/");

        SqlBuilder sb = new SqlBuilder(vanillaSql.toString(), pc);
        return FillProjectCollection(col, sb.compileSql());
    }

    private int FillProjectCollection(ProjectCollection col, String sql) {
        try {
            ResultSet rs = dc.executeQuery(sql);
            while (rs.next()) {
                Project p = getContextualProject(rs.getInt("project_id"));
                if (!col.add(p)) {
                    LoggingInterface.getLogger().logWarning("Project (project_id = %d) could not be added to the collection!", p.getId());
                }
            }
        } catch (Exception ex) {
            LoggingInterface.getInstanece().handleException(ex);
        }
        return col.size();
    }

    private Project getContextualProject(int projectId) throws DalException {
        if (currentContext.hasProject(projectId)) {
            return currentContext.getProject(projectId);
        } else {
            Getable<Project> projectDal = new ProjectDal(ci, currentContext);
            Project project = projectDal.get(new ParameterCollection(new Parameter[] {new Parameter(Parameters.Project.ID, projectId, Sql.Condition.EQUALTY)}));
            return project;
        }
    }


}
