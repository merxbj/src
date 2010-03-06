package notwa.dal;

import notwa.wom.BusinessObjectCollection;
import notwa.common.ConnectionInfo;

public abstract class DataAccessLayer implements Fillable<BusinessObjectCollection> {
	protected DatabaseConnection dc;
	
	protected DataAccessLayer(ConnectionInfo ci) {
		  dc = new DatabaseConnection(ci);
	}
}
