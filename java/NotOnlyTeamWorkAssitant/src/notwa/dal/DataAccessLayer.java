package notwa.dal;

import notwa.wom.BusinessObjectCollection;
import notwa.common.ConnectionInfo;
import notwa.common.LoggingInterface;

public abstract class DataAccessLayer implements Fillable<BusinessObjectCollection> {
	protected DatabaseConnection dc;
	
	public DataAccessLayer() {
		LoggingInterface.getLogger().logWarning("Creating DataAccessLayer subclass with default constructor!");
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
