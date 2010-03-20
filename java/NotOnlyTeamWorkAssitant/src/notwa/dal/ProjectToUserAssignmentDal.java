package notwa.dal;

import notwa.common.ConnectionInfo;
import notwa.sql.ParameterCollection;
import notwa.wom.UserCollection;
import notwa.wom.User;
import notwa.sql.SqlBuilder;
import notwa.common.LoggingInterface;
import notwa.wom.Context;
import notwa.sql.Parameters;
import notwa.sql.Parameter;
import notwa.sql.Sql;
import notwa.exception.DalException;

import java.sql.ResultSet;

public class ProjectToUserAssignmentDal extends DataAccessLayer implements Fillable<UserCollection> {

    private ConnectionInfo ci;

    public ProjectToUserAssignmentDal(ConnectionInfo ci) {
        super(ci);
        this.ci = ci;
    }

    @Override
    public int Fill(UserCollection uc) {
        ParameterCollection emptyPc = new ParameterCollection();
        return Fill(uc, emptyPc);
    }

    @Override
    public int Fill(UserCollection uc, ParameterCollection pc) {
        StringBuilder vanillaSql = new StringBuilder();

        vanillaSql.append("SELECT   pua.user_id AS user_id");
        vanillaSql.append("FROM Project_User_Assignment pua ");
        vanillaSql.append("JOIN Project p ");
        vanillaSql.append("ON p.project_id = pua.project_id ");
        vanillaSql.append("/** STATEMENT=WHERE;RELATION=AND;");
        vanillaSql.append("        {column=project_id;parameter=ProjectId;}");
        vanillaSql.append("**/");

        SqlBuilder sb = new SqlBuilder(vanillaSql.toString(), pc);
        return FillUserCollection(uc, sb.compileSql());
    }

    private int FillUserCollection(UserCollection uc, String sql) {
        try {
            ResultSet rs = dc.executeQuery(sql);
            while (rs.next()) {
                User u = getContextualUser(rs.getInt("user_id"), uc.getCurrentContext());
                if (!uc.add(u)) {
                    LoggingInterface.getLogger().logWarning("Project (project_id = %d) could not be added to the collection!", u.getId());
                }
            }
        } catch (Exception ex) {
            LoggingInterface.getInstanece().handleException(ex);
        }
        return uc.size();
    }

    private User getContextualUser(int userId, Context context) throws DalException {
        if (context.hasUser(userId)) {
            return context.getUser(userId);
        } else {
            Getable<User> userDal = new UserDal(ci);
            User user = userDal.get(new ParameterCollection(new Parameter[] {new Parameter(Parameters.User.ID, userId, Sql.Condition.EQUALTY)}));
            user.registerWithContext(context);
            return user;
        }
    }


}
