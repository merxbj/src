/*
 * NoteDal
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

import notwa.wom.Note;
import notwa.wom.NoteCollection;
import notwa.wom.NotePrimaryKey;
import notwa.common.ConnectionInfo;
import notwa.sql.ParameterSet;
import notwa.sql.Parameter;
import notwa.sql.Parameters;
import notwa.sql.Sql;
import notwa.exception.DalException;
import notwa.wom.Context;
import notwa.wom.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class NoteDal extends DataAccessLayer<Note, NoteCollection> {

    public NoteDal(ConnectionInfo ci, Context context) {
        super(ci, context);
    }

    @Override
    protected String getSqlTemplate() {
        StringBuilder vanillaSql = new StringBuilder();

        vanillaSql.append("SELECT   note_id, ");
        vanillaSql.append("         work_item_id, ");
        vanillaSql.append("         author_user_id, ");
        vanillaSql.append("         note ");
        vanillaSql.append("FROM Work_Item_Note ");
        vanillaSql.append("/** STATEMENT=WHERE;RELATION=AND;");
        vanillaSql.append("        {column=note_id;parameter=NoteId;}");
        vanillaSql.append("        {column=work_item_id;parameter=NoteWorkItemId;}");
        vanillaSql.append("        {column=author_user_id;parameter=NoteAuthorUserId;}");
        vanillaSql.append("**/");

        return vanillaSql.toString();
    }

    @Override
    protected Object getPrimaryKey(ResultSet rs) throws DalException {
        try {
            return new NotePrimaryKey(rs.getInt("note_id"), rs.getInt("work_item_id"));
        } catch (SQLException sex) {
            throw new DalException("Unable to read the note primary key from the database!", sex);
        }
    }

    @Override
    protected ParameterSet getPrimaryKeyParams(Object primaryKey) {
        NotePrimaryKey npk = (NotePrimaryKey) primaryKey;
        Parameter noteId = new Parameter(Parameters.Note.ID, npk.getNoteId(), Sql.Condition.EQUALTY);
        Parameter workItemId = new Parameter(Parameters.Note.WORK_ITEM_ID, npk.getWorkItemId(), Sql.Condition.EQUALTY);
        return new ParameterSet(new Parameter[] {noteId, workItemId});
    }

    @Override
    protected boolean isInCurrentContext(Object primaryKey) throws DalException {
        try {
            return currentContext.hasNote((NotePrimaryKey) primaryKey);
        } catch (Exception ex) {
            throw new DalException("Invalid primary key provided for context query!", ex);
        }
    }

    @Override
    protected Note getBusinessObject(Object primaryKey) throws DalException {
        try {
            return currentContext.getNote((NotePrimaryKey) primaryKey);
        } catch (Exception ex) {
            throw new DalException("Invalid primary key provided for context query!", ex);
        }
    }

    @Override
    protected Note getBusinessObject(Object primaryKey, ResultSet rs) throws DalException {
        try {
            NotePrimaryKey npk = (NotePrimaryKey) primaryKey;

            UserDal userDal = new UserDal(ci, currentContext);
            User author = userDal.get(rs.getInt("author_user_id"));

            Note n = new Note(npk);
            n.registerWithContext(currentContext);
            n.setAuthor(author);
            n.setNoteText(rs.getString("note"));

            return n;
        } catch (Exception ex) {
            throw new DalException("Error while parsing the Note from ResultSet!", ex);
        }
    }

    @Override
    protected void updateSingleRow(ResultSet rs, Note n) throws Exception {
        rs.updateInt("note_id", n.getId().getNoteId());
        rs.updateInt("work_item_id", n.getId().getWorkItemId());
        rs.updateInt("author_user_id", n.getAuthor().getId());
        rs.updateString("note", n.getText());
    }
}
