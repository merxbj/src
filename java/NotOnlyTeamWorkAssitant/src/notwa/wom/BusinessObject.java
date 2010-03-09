package notwa.wom;

import java.lang.reflect.Field;

import notwa.common.LoggingInterface;

public abstract class BusinessObject {
	
	protected BusinessObjectCollection attachedBOC;
	protected BusinessObject originalVersion;
	
	protected String separator = new String (" | ");
	
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
		Class<?> c = this.getClass();
		Class<?> o = originalVersion.getClass();
		
		for (Field field : c.getDeclaredFields()) {
			try {
				field.setAccessible(true);
				for (Field ovField : o.getDeclaredFields()) {
						ovField.setAccessible(true);
						Object value = ovField.get(originalVersion);
						if (field.getName().equals(ovField.getName())) {
							field.set(this, value);
						}
				}
			} catch (Exception e) {
				LoggingInterface.getInstanece().handleException(e);
			}
		}
	}
	
	protected void commit() {
		this.originalVersion = null;
		try {
			this.originalVersion = (BusinessObject) this.clone();
		} catch (CloneNotSupportedException e) {
			LoggingInterface.getInstanece().handleException(e);
		}
	}
}
