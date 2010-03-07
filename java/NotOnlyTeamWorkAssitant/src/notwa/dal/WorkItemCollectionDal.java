package notwa.dal;

import notwa.wom.*;

public class WorkItemCollectionDal extends DataAccessLayer {
	
	public int Fill(WorkItemCollection wic) {
		ParameterCollection emptyPc = new ParameterCollection();
		return Fill(wic, emptyPc);
	}
	
	public int Fill(WorkItemCollection wic, ParameterCollection pc) {
		String vanilaSql = "SELECT * FROM Work_Items"; 
		return 1;
	}
}
