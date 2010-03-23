package notwa.dal;

import notwa.common.ConnectionInfo;
import notwa.sql.ParameterSet;
import notwa.wom.Project;
import notwa.wom.ProjectCollection;
import notwa.sql.SqlBuilder;
import notwa.common.LoggingInterface;
import notwa.exception.DalException;
import notwa.wom.Context;
import notwa.sql.Parameter;
import notwa.wom.UserCollection;
import notwa.sql.Parameters;
import notwa.sql.Sql;

import java.sql.ResultSet;

public class ProjectDal extends DataAccessLayer implements Fillable<ProjectCollection>, Getable<Project> {

    private Context currentContext;
    private ConnectionInfo ci;

    public ProjectDal(ConnectionInfo ci, Context context) {
        super(ci);
        this.currentContext = context;
        this.ci = ci;
    }

    @Override
    public int Fill(ProjectCollection col) {
        ParameterSet emptyPc = new ParameterSet();
        return Fill(col, emptyPc);
    }

    @Override
    public int Fill(ProjectCollection col, ParameterSet pc) {
        StringBuilder vanillaSql = new StringBuilder();

        vanillaSql.append("SELECT   project_id, ");
        vanillaSql.append("         name ");
        vanillaSql.append("FROM Project ");
        vanillaSql.append("/** STATEMENT=WHERE;RELATION=AND;");
        vanillaSql.append("        {column=project_id;parameter=ProjectId;}");
        vanillaSql.append("        {column=name;parameter=ProjectName;}");
        vanillaSql.append("**/");

        SqlBuilder sb = new SqlBuilder(vanillaSql.toString(), pc);
        return FillProjectCollection(col, sb.compileSql());
    }

    private int FillProjectCollection(ProjectCollection col, String sql) {
        try {
            ResultSet rs = dc.executeQuery(sql);
            while (rs.next()) {
                Project p = null;
                int projectId = rs.getInt("project_id");
                if (currentContext.hasProject(projectId)) {
                    p = currentContext.getProject(projectId);
                } else {
                    p = new Project(projectId);
                    p.registerWithContext(currentContext);
                    p.setProjectName(rs.getString("name"));
                    p.setAssignedUsers(getAssignedUserCollection(projectId));
                }
                
                if (!col.add(p)) {
                    LoggingInterface.getLogger().logWarning("Project (project_id = %d) could not be added to the collection!", p.getId());
                }
            }
        } catch (Exception ex) {
            LoggingInterface.getInstanece().handleException(ex);
        }
        return col.size();
    }

    private UserCollection getAssignedUserCollection(int projectId) throws DalException {
        UserCollection uc = new UserCollection(currentContext);
        ProjectToUserAssignmentDal ptuaDal = new ProjectToUserAssignmentDal(ci, currentContext);
        ptuaDal.Fill(uc, new ParameterSet(new Parameter[] {new Parameter(Parameters.Project.ID, projectId, Sql.Condition.EQUALTY)}));
        return uc;
    }

    @Override
    public Project get(ParameterSet primaryKey) throws DalException {
        ProjectCollection pc = new ProjectCollection(currentContext);
        int rows = this.Fill(pc, primaryKey);
        if (rows > 1) {
            throw new DalException("Supplied parameters is not a primary key!");
        } else if (rows == 0) {
            return null;
        }
        return pc.get(0);
    }
}
