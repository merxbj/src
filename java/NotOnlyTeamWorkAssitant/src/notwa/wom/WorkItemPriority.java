package notwa.wom;

import java.util.TreeMap;

public enum WorkItemPriority {
	CRITICAL(4), IMPORTANT(3), NORMAL(2), NICE_TO_HAVE(1), UNNECESSARY(0);
	
	private int value;
	
	WorkItemPriority(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	private static TreeMap<Integer, WorkItemPriority> map;
	static {
		map = new TreeMap<Integer, WorkItemPriority>();
		for (WorkItemPriority wip : WorkItemPriority.values()) {
			map.put(new Integer(wip.getValue()), wip);
		}
	}
	
	public static WorkItemPriority lookup(int value) {
		return map.get(new Integer(value));
	}
}
