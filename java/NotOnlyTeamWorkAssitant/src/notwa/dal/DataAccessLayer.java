package notwa.dal;

import notwa.wom.BusinessObjectCollection;
import notwa.common.ConnectionInfo;

public abstract class DataAccessLayer implements Fillable<BusinessObjectCollection> {
	protected DatabaseConnection dc;
	
	public DataAccessLayer() {
		
		dc = null;
	}
	
	public DataAccessLayer(ConnectionInfo ci) {
		dc = new DatabaseConnection(ci);
	}

	@Override
	public int Fill(BusinessObjectCollection boc, ParameterCollection pc) {
		return 0;
	}

	@Override
	public int Fill(BusinessObjectCollection boc) {
		return 0;
	}
}
