package notwa.dal;

import notwa.common.ConnectionInfo;
import notwa.sql.ParameterCollection;
import notwa.wom.User;
import notwa.wom.UserCollection;
import notwa.sql.SqlBuilder;
import notwa.wom.ProjectCollection;
import notwa.sql.Parameter;
import notwa.sql.Parameters;
import notwa.sql.Sql;
import notwa.common.LoggingInterface;
import notwa.exception.DalException;

import java.sql.ResultSet;

public class UserDal extends DataAccessLayer implements Fillable<UserCollection>, Getable<User> {

    public UserDal(ConnectionInfo ci) {
        super(ci);
    }

    @Override
    public int Fill(UserCollection uc) {
        ParameterCollection emptyPc = new ParameterCollection();
        return Fill(uc, emptyPc);
    }

    @Override
    public int Fill(UserCollection uc, ParameterCollection pc) {
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
        return FillProjectCollection(uc, sb.compileSql());
    }

    private int FillProjectCollection(UserCollection uc, String sql) {
        try {
            ResultSet rs = dc.executeQuery(sql);
            while (rs.next()) {
                User u = new User(rs.getInt("user_id"));
                u.setFirstName(rs.getString("first_name"));
                u.setLastName(rs.getString("last_name"));
                u.setLogin(rs.getString("login"));
                u.setPassword(rs.getString("password"));
                if (!uc.add(u)) {
                    LoggingInterface.getLogger().logWarning("User (user_id = %d) could not be added to the collection!", u.getId());
                }
            }
        } catch (Exception ex) {
            LoggingInterface.getInstanece().handleException(ex);
        }
        return uc.size();
    }

    @Override
    public User get(ParameterCollection primaryKey) throws DalException {
        UserCollection uc = new UserCollection();
        int rows = this.Fill(uc, primaryKey);
        if (rows > 1) {
            throw new DalException("Supplied parameters is not a primary key!");
        }
        return uc.get(0);
    }
}
