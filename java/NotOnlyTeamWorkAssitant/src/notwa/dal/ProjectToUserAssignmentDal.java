/*
 * NoteCollection
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
import notwa.exception.DalException;
import notwa.wom.UserCollection;
import notwa.wom.User;
import notwa.wom.AssignedUser;
import notwa.wom.Context;
import notwa.sql.Parameters;
import notwa.sql.ParameterSet;
import notwa.sql.Sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import notwa.sql.Parameter;

/**
 * <code>ProjectToUserAssignmentDal</code> is a <code>DataAccessLayer</code>
 * concrete implementation providing the actual data and methods how to maintain
 * the user assignment data persisted in the database.
 * <p>The actuall workflow is maintained by the base class itself.</p>
 * <p>This DAL doesn't support the {@link #get(java.lang.Object)} method</p>
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class ProjectToUserAssignmentDal extends DataAccessLayer<User, UserCollection> {

    public ProjectToUserAssignmentDal(ConnectionInfo ci, Context context) {
        super(ci, context);
        this.currentContext = context;
    }

    @Override
    protected String getSqlTemplate() {
        StringBuilder vanillaSql = new StringBuilder();

        vanillaSql.append("SELECT   pua.user_id AS user_id ");
        vanillaSql.append("FROM Project_User_Assignment pua ");
        vanillaSql.append("JOIN Project p ");
        vanillaSql.append("ON p.project_id = pua.project_id ");
        vanillaSql.append("/** STATEMENT=WHERE;RELATION=AND;");
        vanillaSql.append("        {column=pua.project_id;parameter=ProjectId;}");
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
    protected boolean isInCurrentContext(Object primaryKey) throws DalException {
        try {
            return currentContext.hasUser((Integer) primaryKey);
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
        UserDal userDal = new UserDal(ci, currentContext);
        return userDal.get(primaryKey);
    }

    @Override
    public User get(Object primaryKey) throws DalException {
        throw new DalException("This DataAccessLayer doesn't support operation get.");
    }

    @Override
    protected void updateSingleRow(ResultSet rs, User u) throws Exception {
        /*
         * We should always make project to user assignment update on assignment
         * aware User!
         */
        AssignedUser au = (AssignedUser) u;
        rs.updateInt("project_id", au.getAssignedProject().getId());
        rs.updateInt("user_id", au.getId());
    }
}
