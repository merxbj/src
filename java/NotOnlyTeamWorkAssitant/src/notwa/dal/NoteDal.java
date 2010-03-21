package notwa.dal;

import notwa.wom.Note;
import notwa.wom.NoteCollection;
import notwa.wom.NotePrimaryKey;
import notwa.common.ConnectionInfo;
import notwa.sql.ParameterCollection;
import notwa.sql.Parameter;
import notwa.sql.Parameters;
import notwa.sql.Sql;
import notwa.sql.SqlBuilder;
import notwa.common.LoggingInterface;
import notwa.exception.DalException;
import notwa.wom.Context;
import notwa.wom.User;

import java.sql.ResultSet;

public class NoteDal extends DataAccessLayer implements Fillable<NoteCollection>, Getable<Note> {

    private Context currentContext;
    private ConnectionInfo ci;

    public NoteDal(ConnectionInfo ci, Context context) {
        super(ci);
        this.currentContext = context;
        this.ci = ci;
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
                Note n = null;
                NotePrimaryKey npk = new NotePrimaryKey(rs.getInt("note_id"), rs.getInt("work_item_id"));
                if (currentContext.hasNote(npk)) {
                    n = currentContext.getNote(npk);
                } else {
                    Getable<User> userDal = new UserDal(ci, currentContext);
                    User author = userDal.get(new ParameterCollection(new Parameter[] {new Parameter(Parameters.User.ID, rs.getInt("author_user_id"), Sql.Condition.EQUALTY)}));

                    n = new Note(npk);
                    n.registerWithContext(currentContext);
                    n.setAuthor(author);
                    n.setNoteText(rs.getString("note"));
                }
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
        NoteCollection nc = new NoteCollection(currentContext);
        int rows = this.Fill(nc, primaryKey);
        if (rows > 1) {
            throw new DalException("Supplied parameters is not a primary key!");
        } else if (rows == 0) {
            return null;
        }
        return nc.get(0);
    }
}
