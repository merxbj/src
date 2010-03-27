/*
 * UserDal
 *
 * Copyright (C) 2010  Jaroslav Merxbauer
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package notwa.dal;

import notwa.common.ConnectionInfo;
import notwa.common.LoggingInterface;
import notwa.sql.ParameterSet;
import notwa.sql.SqlBuilder;
import notwa.sql.Parameter;
import notwa.sql.Parameters;
import notwa.sql.Sql;
import notwa.wom.User;
import notwa.wom.UserCollection;
import notwa.wom.ProjectCollection;
import notwa.wom.Context;
import notwa.exception.DalException;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Layer providing an access to the User sql table.
 * It provides a several methods to:
 * <ul>
 * <li>Fill the given <code>UserCollection</code></li>
 * <li>Retrieve the single <code>User</code> or finally</li>
 * <li>Get the password for the given login.</li>
 *
 * @author eTeR
 * @version %I% %G%
 */
public class UserDal extends DataAccessLayer implements Fillable<UserCollection>, Getable<User> {

    public UserDal(ConnectionInfo ci, Context context) {
        super(ci, context);
    }

    /**
     * Fills the given <code>UserCollection</code> with all possible data.
     * 
     * @param uc The <code>UserCollection</code> to fill.
     * @return The number of <code>User</code>s filled into the <code>Collection</code>.
     */
    @Override
    public int Fill(UserCollection uc) {
        ParameterSet emptyPc = new ParameterSet();
        return Fill(uc, emptyPc);
    }

    /**
     * Fills the given <code>UserCollection</code> with data based on the given
     * <code>ParameterSet</code>.
     * @see Parameters
     *
     * @param uc The <code>UserCollection</code> to fill.
     * @return The number of <code>User</code>s filled into the <code>Collection</code>.
     */
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

    /**
     * Fills the given <code>UserCollection</code> based upon the given sql string.
     * If user with the same user id already lives in the same context as this
     * <code>DataAccessLayer</code> it is rather picked up from the context than
     * recreated again
     *
     * @param uc <code>UserCollection</code> to be filled.
     * @param sql Already parametrized SQL Query.
     * @return Number of the actual <code>User</code>s filled into the <code>Collection</code>
     */
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

    /**
     * Builds up the <code>ProjectCollection</code> based on the <code>User</code>
     * who they are assigned to.
     *
     * @param userId The actual <code>User</code> who has assigned requested <code>Project</code>s.
     * @return  The <code>ProjectCollection</code> of <code>Project</code>s assigned to the
     *          given <code>User</code>.
     * @throws DalException Whenever the issue with the database access occure.
     */
    private ProjectCollection getAssignedProjectCollection(int userId) throws DalException {
        ProjectCollection pc = new ProjectCollection(currentContext);
        UserToProjectAssignmentDal utpaDal = new UserToProjectAssignmentDal(ci, currentContext);
        utpaDal.Fill(pc, new ParameterSet(new Parameter(Parameters.User.ID, userId, Sql.Condition.EQUALTY)));
        return pc;
    }

    /**
     * Gets the single <code>User</code> from the database based on the given
     * <code>ParameterSet</code> which should actually represent the user primary key.
     * If user the same primary key is alread present in the current context, it is
     * rather picked up from that context instead of creating a new one and wasting the
     * time with waiting for the database to process the query.
     *
     * @param primaryKey It should be the user id of requested user.
     * @return  The requested <code>User</code> instance.
     *          <code>null</code if there is no such a <code>User</code>.
     * @throws DalException Whenever the given <code>ParameterSet</code> doesn't
     *                      represent the requested primary key or a database
     *                      connection issue occures.
     */
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

    /**
     * Gets the password assigned to the user based on the the supplied login.
     *
     * @param login The actual login of the <code>User</code> who is attempting to
     *              log in to the database.
     * @return Always not <code>null</code> password string.
     */
    public String get(String login) {
        String sql = String.format("SELECT password FROM User WHERE login = '%s'", login);
        String password = null;
        try {
            password = (String) getConnection().executeScalar(sql);
        } catch (SQLException sex) {
            LoggingInterface.getInstanece().handleException(sex);
        }
        return (password != null) ? password : "";
    }
}
