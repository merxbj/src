package notwa.dal;

import notwa.common.ConnectionInfo;
import notwa.sql.ParameterCollection;
import notwa.wom.Project;
import notwa.wom.ProjectCollection;
import notwa.sql.SqlBuilder;
import notwa.common.LoggingInterface;
import notwa.exception.DalException;

import java.sql.ResultSet;

public class ProjectDal extends DataAccessLayer implements Fillable<ProjectCollection>, Getable<Project> {

    public ProjectDal(ConnectionInfo ci) {
        super(ci);
    }

    @Override
    public int Fill(ProjectCollection col) {
        ParameterCollection emptyPc = new ParameterCollection();
        return Fill(col, emptyPc);
    }

    @Override
    public int Fill(ProjectCollection col, ParameterCollection pc) {
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
                Project p = new Project(rs.getInt("project_id"));
                p.setProjectName(rs.getString("project_name"));
                if (!col.add(p)) {
                    LoggingInterface.getLogger().logWarning("Project (project_id = %d) could not be added to the collection!", p.getId());
                }
            }
        } catch (Exception ex) {
            LoggingInterface.getInstanece().handleException(ex);
        }
        return col.size();
    }

    @Override
    public Project get(ParameterCollection primaryKey) throws DalException {
        ProjectCollection pc = new ProjectCollection();
        int rows = this.Fill(pc, primaryKey);
        if (rows > 1) {
            throw new DalException("Supplied parameters is not a primary key!");
        }
        return pc.get(0);
    }
}
