package notwa.dal;

import notwa.common.ConnectionInfo;
import notwa.sql.ParameterSet;
import notwa.wom.ProjectCollection;
import notwa.wom.Project;
import notwa.sql.SqlBuilder;
import notwa.common.LoggingInterface;
import notwa.wom.Context;
import notwa.sql.Parameters;
import notwa.sql.Parameter;
import notwa.sql.Sql;

import java.sql.ResultSet;

public class UserToProjectAssignmentDal extends DataAccessLayer implements Fillable<ProjectCollection> {

    public UserToProjectAssignmentDal(ConnectionInfo ci, Context context) {
        super(ci, context);
    }

    @Override
    public int Fill(ProjectCollection col) {
        ParameterSet emptyPc = new ParameterSet();
        return Fill(col, emptyPc);
    }

    @Override
    public int Fill(ProjectCollection col, ParameterSet pc) {
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
            Getable<Project> projectDal = new ProjectDal(ci, currentContext);
            ResultSet rs = getConnection().executeQuery(sql);
            while (rs.next()) {
                Project p = projectDal.get(new ParameterSet(new Parameter(Parameters.Project.ID, rs.getInt("pua.project_id"), Sql.Condition.EQUALTY)));
                if (!col.add(p)) {
                    LoggingInterface.getLogger().logWarning("Project (project_id = %d) could not be added to the collection!", p.getId());
                }
            }
        } catch (Exception ex) {
            LoggingInterface.getInstanece().handleException(ex);
        }
        return col.size();
    }
}
