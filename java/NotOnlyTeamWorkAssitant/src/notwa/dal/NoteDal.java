package notwa.dal;

import notwa.wom.Note;
import notwa.wom.NoteCollection;
import notwa.common.ConnectionInfo;
import notwa.sql.ParameterCollection;
import notwa.wom.Project;
import notwa.wom.ProjectCollection;
import notwa.sql.SqlBuilder;
import notwa.common.LoggingInterface;
import notwa.exception.DalException;

import java.sql.ResultSet;

public class NoteDal extends DataAccessLayer implements Fillable<NoteCollection>, Getable<Note> {

    public NoteDal(ConnectionInfo ci) {
        super(ci);
    }

    @Override
    public int Fill(NoteCollection nc) {
        ParameterCollection emptyPc = new ParameterCollection();
        return Fill(nc, emptyPc);
    }

    @Override
    public int Fill(NoteCollection nc, ParameterCollection pc) {
        
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

        SqlBuilder sb = new SqlBuilder(vanillaSql.toString(), pc);
        return FillProjectCollection(nc, sb.compileSql());
    }

    private int FillProjectCollection(NoteCollection nc, String sql) {
        try {
            ResultSet rs = dc.executeQuery(sql);
            while (rs.next()) {
                
                Note n = new Note(rs.getInt("note_id"));
                n.setAuthor(null);
                if (!nc.add(n)) {
                    LoggingInterface.getLogger().logWarning("Project (project_id = %d) could not be added to the collection!", n.getNoteText());
                }
            }
        } catch (Exception ex) {
            LoggingInterface.getInstanece().handleException(ex);
        }
        return nc.size();
    }

    @Override
    public Note get(ParameterCollection primaryKey) throws DalException {
        NoteCollection nc = new NoteCollection();
        int rows = this.Fill(nc, primaryKey);
        if (rows > 1) {
            throw new DalException("Supplied parameters is not a primary key!");
        }
        return nc.get(0);
    }
}
