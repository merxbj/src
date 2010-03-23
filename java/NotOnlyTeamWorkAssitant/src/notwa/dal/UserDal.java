package notwa.dal;

import notwa.common.ConnectionInfo;
import notwa.sql.ParameterSet;
import notwa.wom.User;
import notwa.wom.UserCollection;
import notwa.sql.SqlBuilder;
import notwa.wom.ProjectCollection;
import notwa.sql.Parameter;
import notwa.sql.Parameters;
import notwa.sql.Sql;
import notwa.common.LoggingInterface;
import notwa.exception.DalException;
import notwa.wom.Context;

import java.sql.ResultSet;

public class UserDal extends DataAccessLayer implements Fillable<UserCollection>, Getable<User> {

    public UserDal(ConnectionInfo ci, Context context) {
        super(ci, context);
    }

    @Override
    public int Fill(UserCollection uc) {
        ParameterSet emptyPc = new ParameterSet();
        return Fill(uc, emptyPc);
    }

    @Override
    public int Fill(UserCollection uc, ParameterSet pc) {
        StringBuilder vanillaSql = new StringBuilder();

        vanillaSql.append("SELECT   user_id, ");
        vanillaSql.append("         login, ");
        vanillaSql.append("         password, ");
        vanillaSql.append("         first_name, ");
        vanillaSql.append("         last_name ");
        vanillaSql.append("FROM User ");
        vanillaSql.append("/** STATEMENT=WHERE;RELATION=AND;");
        vanillaSql.append("        {column=user_id;parameter=UserId;}");
        vanillaSql.append("        {column=login;parameter=UserLogin;}");
        vanillaSql.append("        {column=password;parameter=UserPassword;}");
        vanillaSql.append("        {column=first_name;parameter=UserFirstName;}");
        vanillaSql.append("        {column=last_name;parameter=UserLastName;}");
        vanillaSql.append("**/");

        SqlBuilder sb = new SqlBuilder(vanillaSql.toString(), pc);
        return FillUserCollection(uc, sb.compileSql());
    }

    private int FillUserCollection(UserCollection uc, String sql) {
        try {
            ResultSet rs = getConnection().executeQuery(sql);
            while (rs.next()) {
                User u = null;
                int userId = rs.getInt("user_id");
                if (currentContext.hasUser(userId)) {
                    u = currentContext.getUser(userId);
                } else {
                    u = new User(userId);
                    u.registerWithContext(currentContext);
                    u.setFirstName(rs.getString("first_name"));
                    u.setLastName(rs.getString("last_name"));
                    u.setLogin(rs.getString("login"));
                    u.setPassword(rs.getString("password"));
                    u.setAssignedProjects(getAssignedProjectCollection(userId));
                }
                if (!uc.add(u)) {
                    LoggingInterface.getLogger().logWarning("User (user_id = %d) could not be added to the collection!", u.getId());
                }
            }
        } catch (Exception ex) {
            LoggingInterface.getInstanece().handleException(ex);
        }
        return uc.size();
    }

    private ProjectCollection getAssignedProjectCollection(int userId) throws DalException {
        ProjectCollection pc = new ProjectCollection(currentContext);
        UserToProjectAssignmentDal utpaDal = new UserToProjectAssignmentDal(ci, currentContext);
        utpaDal.Fill(pc, new ParameterSet(new Parameter(Parameters.User.ID, userId, Sql.Condition.EQUALTY)));
        return pc;
    }

    @Override
    public User get(ParameterSet primaryKey) throws DalException {
        Parameter p = primaryKey.first();
        if (p.getName().equals(Parameters.User.ID)) {
            int userId = (Integer) p.getValue();
            if (currentContext.hasUser(userId)) {
                return currentContext.getUser(userId);
            } else {
                UserCollection uc = new UserCollection(currentContext);
                int rows = this.Fill(uc, primaryKey);
                if (rows == 1) {
                    return uc.get(0);
                } else if (rows == 0) {
                    return null;
                }
            }
        }
        throw new DalException("Supplied parameters are not a primary key!");
    }

    public User get(String login) {
        UserCollection uc = new UserCollection(currentContext);
        int rows = this.Fill(uc, new ParameterSet(new Parameter(Parameters.User.LOGIN, login, Sql.Condition.EQUALTY)));
        if (rows == 1) {
            return uc.get(0);
        } else {
            return null;
        }
    }
}
