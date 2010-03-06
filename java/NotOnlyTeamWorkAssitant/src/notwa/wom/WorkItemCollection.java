package notwa.wom;

import java.util.ArrayList;

public class WorkItemCollection {

	ArrayList<WorkItem> workItemCollection = new ArrayList<WorkItem>();
	
	public void add(WorkItem wi) {
		workItemCollection.add(wi);
	}
}
