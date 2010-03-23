package notwa.dal;

import notwa.common.ConnectionInfo;
import notwa.wom.UserCollection;
import notwa.wom.User;
import notwa.sql.SqlBuilder;
import notwa.common.LoggingInterface;
import notwa.wom.Context;
import notwa.sql.Parameters;
import notwa.sql.ParameterSet;
import notwa.sql.Sql;

import java.sql.ResultSet;
import notwa.sql.Parameter;

public class ProjectToUserAssignmentDal extends DataAccessLayer implements Fillable<UserCollection> {

    public ProjectToUserAssignmentDal(ConnectionInfo ci, Context context) {
        super(ci, context);
        this.currentContext = context;
    }

    @Override
    public int Fill(UserCollection uc) {
        ParameterSet emptyPc = new ParameterSet();
        return Fill(uc, emptyPc);
    }

    @Override
    public int Fill(UserCollection uc, ParameterSet pc) {
        StringBuilder vanillaSql = new StringBuilder();

        vanillaSql.append("SELECT   pua.user_id AS user_id ");
        vanillaSql.append("FROM Project_User_Assignment pua ");
        vanillaSql.append("JOIN Project p ");
        vanillaSql.append("ON p.project_id = pua.project_id ");
        vanillaSql.append("/** STATEMENT=WHERE;RELATION=AND;");
        vanillaSql.append("        {column=pua.project_id;parameter=ProjectId;}");
        vanillaSql.append("**/");

        SqlBuilder sb = new SqlBuilder(vanillaSql.toString(), pc);
        return FillUserCollection(uc, sb.compileSql());
    }

    private int FillUserCollection(UserCollection uc, String sql) {
        try {
            Getable<User> ud = new UserDal(ci, currentContext);
            ResultSet rs = getConnection().executeQuery(sql);
            while (rs.next()) {
                User u = ud.get(new ParameterSet(new Parameter(Parameters.User.ID, rs.getInt("user_id"), Sql.Condition.EQUALTY)));
                if (!uc.add(u)) {
                    LoggingInterface.getLogger().logWarning("Project (project_id = %d) could not be added to the collection!", u.getId());
                }
            }
        } catch (Exception ex) {
            LoggingInterface.getInstanece().handleException(ex);
        }
        return uc.size();
    }
}
