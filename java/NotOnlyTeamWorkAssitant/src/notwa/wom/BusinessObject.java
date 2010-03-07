package notwa.wom;

import notwa.common.LoggingInterface;

public abstract class BusinessObject {
	
	protected BusinessObjectCollection attachedBOC;
	protected BusinessObject originalVersion;
	
	public void attach(BusinessObjectCollection boc) {
		this.attachedBOC = boc;
		try {
			this.originalVersion = (BusinessObject) this.clone();
		} catch (CloneNotSupportedException ex) {
			LoggingInterface.getInstanece().handleException(ex);
		}
	}
	
	public void detach() {
		this.attachedBOC = null;
	}
	
	public boolean isAttached() {
		return attachedBOC != null;
	}
	
	protected void rollback() {
		
	}
	
	protected void commit() {
		
	}
}
