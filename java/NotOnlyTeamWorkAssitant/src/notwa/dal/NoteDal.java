package notwa.dal;

import notwa.common.ConnectionInfo;
import notwa.sql.ParameterCollection;
import notwa.wom.Note;
import notwa.wom.NoteCollection;

public class NoteDal extends DataAccessLayer implements Fillable<NoteCollection>, Getable<Note> {

	public NoteDal(ConnectionInfo ci) {
		super(ci);
	}

	@Override
	public int Fill(NoteCollection boc, ParameterCollection pc) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int Fill(NoteCollection boc) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Note get(ParameterCollection primaryKey) {
		// TODO Auto-generated method stub
		return new Note(1);
	}
}
