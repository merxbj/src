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
import notwa.logger.LoggingFacade;
import notwa.sql.ParameterSet;
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
import notwa.security.Credentials;

/**
 * <code>UserDal</code> is a <code>DataAccessLayer</code> concrete implementation
 * providing the actual data and methods how to maintain the user data persisted
 * in the database.
 * <p>The actuall workflow is maintained by the base class itself.</p>
 * 
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class UserDal extends DataAccessLayer<User, UserCollection> {

    /**
     * The sole constructor delegating all the work to the base <code>class</code>.
     *
     * @param ci    The <code>ConnectionInfo</code> which refers the actual database
     *              where we want to collect data from.
     * @param context   The actual <code>Context</code> where we want to let the DAL
     *                  live its pittyful life of collectiong data.
     */
    public UserDal(ConnectionInfo ci, Context context) {
        super(ci, context);
    }

    @Override
    protected String getSqlTemplate() {
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

        return vanillaSql.toString();
    }

    @Override
    protected Object getPrimaryKey(ResultSet rs) throws DalException {
        try {
            return rs.getInt("user_id");
        } catch (SQLException sex) {
            throw new DalException("Unable to read the user id from the database!", sex);
        }
    }

    @Override
    protected ParameterSet getPrimaryKeyParams(Object primaryKey) {
        return new ParameterSet(new Parameter(Parameters.User.ID, primaryKey, Sql.Condition.EQUALTY));
    }
    
    @Override
    protected boolean isInCurrentContext(Object pk) throws DalException {
        try {
            return currentContext.hasUser((Integer) pk);
        } catch (Exception ex) {
            throw new DalException("Invalid primary key provided for context query!", ex);
        }
    }

    @Override
    protected User getBusinessObject(Object primaryKey) throws DalException {
        try {
            return currentContext.getUser((Integer) primaryKey);
        } catch (Exception ex) {
            throw new DalException("Invalid primary key provided for context query!", ex);
        }
    }

    @Override
    protected User getBusinessObject(Object primaryKey, ResultSet rs) throws DalException {
        try {
            int userId = (Integer) primaryKey;

            User u = new User(userId);
            u.registerWithContext(currentContext);
            u.setFirstName(rs.getString("first_name"));
            u.setLastName(rs.getString("last_name"));
            u.setLogin(rs.getString("login"));
            u.setPassword(rs.getString("password"));
            u.setAssignedProjects(getAssignedProjectCollection(userId));

            return u;
        } catch (Exception ex) {
            throw new DalException("Error while parsing the User from ResultSet!", ex);
        }
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
        utpaDal.fill(pc, new ParameterSet(new Parameter(Parameters.User.ID, userId, Sql.Condition.EQUALTY)));
        return pc;
    }

    @Override
    protected void updateSingleRow(ResultSet rs, User u) throws Exception {
        rs.updateInt("user_id", u.getId());
        rs.updateString("first_name", u.getFirstName());
        rs.updateString("last_name", u.getLastName());
        rs.updateString("password", u.getPassword());
    }

    /**
     * Validates that given <code>Credentials</code> are valid in scope of actual
     * database connection. If yes, the actual user id of user, identified
     * by login, is suplied within the credentials instance.
     *
     * @param credentials   The actual credentials of user who is attempting to
     *                      connect to the work item database.
     * @return  <code>true</code> if the credentials were valid, <code>false</code>
     *          otherwise.
     */
    public boolean validateCredentials(Credentials credentials) {
        String sql = String.format("SELECT password, user_id FROM User WHERE login = '%s'", credentials.getLogin());
        String password = null;
        int userId = 0;

        try {
            ResultSet rs = getConnection().executeQuery(sql);
            if (rs.next()) {
                password = rs.getString("password");
                userId = rs.getInt("user_id");
            }
        } catch (SQLException sex) {
            LoggingFacade.getInstanece().handleException(sex);
            return false;
        }

        if (password != null && password.equals(credentials.getPassword())) {
            credentials.setUserId(userId);
            credentials.setValid(true);
        }

        return credentials.isValid();
    }

    @Override
    protected String getHighestUniqeIdentifierSql(User bo) {
        return "SELECT user_id FROM User ORDER BY user_id DESC";
    }
}
