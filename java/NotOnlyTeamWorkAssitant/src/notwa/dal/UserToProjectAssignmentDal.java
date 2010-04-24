/*
 * UserToProjectAssignmentDal
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
import notwa.sql.ParameterSet;
import notwa.wom.ProjectCollection;
import notwa.wom.Project;
import notwa.wom.AssignedProject;
import notwa.wom.Context;
import notwa.sql.Parameters;
import notwa.sql.Parameter;
import notwa.sql.Sql;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <code>UserToProjectAssignmentDal</code> is a <code>DataAccessLayer</code>
 * concrete implementation providing the actual data and methods how to maintain
 * the project assignment data persisted in the database.
 * <p>The actuall workflow is maintained by the base class itself.</p>
 * <p>This DAL doesn't support the {@link #get(java.lang.Object)} method</p>
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class UserToProjectAssignmentDal extends DataAccessLayer<Project, ProjectCollection> {

    /**
     * The sole constructor delegating all the work to the base <code>class</code>.
     *
     * @param ci    The <code>ConnectionInfo</code> which refers the actual database
     *              where we want to collect data from.
     * @param context   The actual <code>Context</code> where we want to let the DAL
     *                  live its pittyful life of collectiong data.
     */
    public UserToProjectAssignmentDal(ConnectionInfo ci, Context context) {
        super(ci, context);
    }

    @Override
    protected String getSqlTemplate() {
        StringBuilder vanillaSql = new StringBuilder();

        vanillaSql.append("SELECT   pua.project_id AS project_id, ");
        vanillaSql.append("         pua.user_id    AS user_id ");
        vanillaSql.append("FROM Project_User_Assignment pua ");
        vanillaSql.append("JOIN User u ");
        vanillaSql.append("ON u.user_id = pua.user_id ");
        vanillaSql.append("/** STATEMENT=WHERE;RELATION=AND;");
        vanillaSql.append("        {column=pua.user_id;parameter=UserId;}");
        vanillaSql.append("**/");

        return vanillaSql.toString();
    }

    @Override
    protected Object getPrimaryKey(ResultSet rs) throws DalException {
        try {
            return rs.getInt("project_id");
        } catch (SQLException sex) {
            throw new DalException("Unable to read the project id from the database!", sex);
        }
    }

    @Override
    protected ParameterSet getPrimaryKeyParams(Object primaryKey) {
        return new ParameterSet(new Parameter(Parameters.Project.ID, primaryKey, Sql.Relation.EQUALTY));
    }

    @Override
    protected boolean isInCurrentContext(Object primaryKey) throws DalException {
        try {
            return currentContext.hasProject((Integer) primaryKey);
        } catch (Exception ex) {
            throw new DalException("Invalid primary key provided for context query!", ex);
        }
    }

    @Override
    protected Project getBusinessObject(Object primaryKey) throws DalException {
        try {
            return currentContext.getProject((Integer) primaryKey);
        } catch (Exception ex) {
            throw new DalException("Invalid primary key provided for context query!", ex);
        }
    }

    @Override
    protected Project getBusinessObject(Object primaryKey, ResultSet rs) throws DalException {
        ProjectDal projectDal = new ProjectDal(ci, currentContext);
        return projectDal.get(primaryKey);
    }

    @Override
    public Project get(Object primaryKey) throws DalException {
        throw new DalException("This DataAccessLayer doesn't support operation get.");
    }

    @Override
    protected void updateSingleRow(ResultSet rs, Project p) throws Exception {
        /*
         * We should always make project to user assignment update on assignment
         * aware User!
         */
        AssignedProject ap = (AssignedProject) p;
        rs.updateInt("user_id", ap.getUser().getId());
        rs.updateInt("project_id", ap.getId());
    }

    @Override
    protected String getHighestUniqeIdentifierSql(Project bo) {
        throw new UnsupportedOperationException("This table has not uniqe identifier!");
    }
}
