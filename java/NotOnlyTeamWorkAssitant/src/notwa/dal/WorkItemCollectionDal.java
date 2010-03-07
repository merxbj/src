package notwa.dal;

import notwa.wom.*;
import java.lang.StringBuilder;

public class WorkItemCollectionDal extends DataAccessLayer {
	
	public int Fill(WorkItemCollection wic) {
		ParameterCollection emptyPc = new ParameterCollection();
		return Fill(wic, emptyPc);
	}
	
	public int Fill(WorkItemCollection wic, ParameterCollection pc) {
		StringBuilder vanillaSql = new StringBuilder();
		vanillaSql.append("SELECT * ");
		vanillaSql.append("FROM Work_Item");
		vanillaSql.append("FROM Work_Item");
		return 1;
	}
}
