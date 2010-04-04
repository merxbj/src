/*
 * ProjectDal
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
import notwa.sql.ParameterSet;
import notwa.wom.Project;
import notwa.wom.ProjectCollection;
import notwa.exception.DalException;
import notwa.wom.Context;
import notwa.sql.Parameter;
import notwa.wom.UserCollection;
import notwa.sql.Parameters;
import notwa.sql.Sql;

import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * <code>ProjectDal</code> is a <code>DataAccessLayer</code> concrete implementation
 * providing the actual data and methods how to maintain the project data persisted
 * in the database.
 * <p>The actuall workflow is maintained by the base class itself.</p>
 * 
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class ProjectDal extends DataAccessLayer<Project, ProjectCollection> {

    public ProjectDal(ConnectionInfo ci, Context context) {
        super(ci, context);
    }

    @Override
    protected String getSqlTemplate() {
        StringBuilder vanillaSql = new StringBuilder();

        vanillaSql.append("SELECT   project_id, ");
        vanillaSql.append("         name ");
        vanillaSql.append("FROM Project ");
        vanillaSql.append("/** STATEMENT=WHERE;RELATION=AND;");
        vanillaSql.append("        {column=project_id;parameter=ProjectId;}");
        vanillaSql.append("        {column=name;parameter=ProjectName;}");
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
        return new ParameterSet(new Parameter(Parameters.Project.ID, primaryKey, Sql.Condition.EQUALTY));
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
        try {
            int projectId = (Integer) primaryKey;

            Project p = new Project(projectId);
            p.registerWithContext(currentContext);
            p.setProjectName(rs.getString("name"));
            p.setAssignedUsers(getAssignedUserCollection(projectId));

            return p;
        } catch (Exception ex) {
            throw new DalException("Error while parsing the Project from ResultSet!", ex);
        }
    }

    private UserCollection getAssignedUserCollection(int projectId) throws DalException {
        UserCollection uc = new UserCollection(currentContext);
        ProjectToUserAssignmentDal ptuaDal = new ProjectToUserAssignmentDal(ci, currentContext);
        ptuaDal.fill(uc, new ParameterSet(new Parameter(Parameters.Project.ID, projectId, Sql.Condition.EQUALTY)));
        return uc;
    }
    
    @Override
    protected void updateSingleRow(ResultSet rs, Project p) throws Exception {
        rs.updateInt("project_id", p.getId());
        rs.updateString("name", p.getName());
    }
}
