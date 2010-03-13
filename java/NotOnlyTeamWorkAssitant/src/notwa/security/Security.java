package notwa.security;

import notwa.common.Config;
import notwa.common.ConnectionInfo;
import notwa.common.LoggingInterface;
import notwa.dal.WorkItemDal;
import notwa.wom.WorkItemCollection;

public class Security {
	private static Security singleton;
	
	private Security() {};
	
	public static Security getInstance() {
		if (singleton == null) {
			singleton = new Security();
		}
		return singleton;
	}
	
	public boolean signIn() {
		try {
			Config.getInstance().parse();
		} catch (Exception ex) {
			LoggingInterface.getInstanece().handleException(ex);
		}
		ConnectionInfo ci = new ConnectionInfo();
		WorkItemDal wid = new WorkItemDal(ci);
		WorkItemCollection wic = new WorkItemCollection();
		wid.Fill(wic);
		
		return false; //na vic uz dneska nemam, gn ;-)
	}
}
