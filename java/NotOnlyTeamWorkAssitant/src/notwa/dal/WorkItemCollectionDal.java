package notwa.dal;

import notwa.wom.*;

public class WorkItemCollectionDal implements Fillable<WorkItemCollection> {
	
	public int Fill(WorkItemCollection wic) {
		ParameterCollection emptyPc = new ParameterCollection();
		return Fill(wic, emptyPc);
	}
	
	public int Fill(WorkItemCollection wic, ParameterCollection pc) {
		return 1;
	}
}
