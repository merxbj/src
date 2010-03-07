package notwa.wom;

public abstract class BusinessObject {
	
	private BusinessObjectCollection attachedBOC = null;
	private Object originalVersion;
	
	public void attach(BusinessObjectCollection boc) {
		this.attachedBOC = boc;
		try {
			this.originalVersion = this.clone();
		} catch (CloneNotSupportedException e) {
			// log?
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
