package notwa.dal;

import notwa.common.ConnectionInfo;
import notwa.sql.ParameterSet;
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
    private Context currentContext;

    public ProjectToUserAssignmentDal(ConnectionInfo ci, Context context) {
        super(ci);
        this.ci = ci;
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
            ResultSet rs = dc.executeQuery(sql);
            while (rs.next()) {
                User u = getContextualUser(rs.getInt("user_id"));
                if (!uc.add(u)) {
                    LoggingInterface.getLogger().logWarning("Project (project_id = %d) could not be added to the collection!", u.getId());
                }
            }
        } catch (Exception ex) {
            LoggingInterface.getInstanece().handleException(ex);
        }
        return uc.size();
    }

    private User getContextualUser(int userId) throws DalException {
        if (currentContext.hasUser(userId)) {
            return currentContext.getUser(userId);
        } else {
            Getable<User> userDal = new UserDal(ci, currentContext);
            User user = userDal.get(new ParameterSet(new Parameter[] {new Parameter(Parameters.User.ID, userId, Sql.Condition.EQUALTY)}));
            return user;
        }
    }


}
