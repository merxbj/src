package notwa.wom;

import java.util.TreeMap;

public enum WorkItemStatus {
    PLEASE_RESOLVE(1),    WAITING(2), IN_PROGRESS(3), CLOSED(4), DELETED(5);
    
    private int value;
    
    WorkItemStatus(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return this.value;
    }
    
    private static TreeMap<Integer, WorkItemStatus> map;
    static {
        map = new TreeMap<Integer, WorkItemStatus>();
        for (WorkItemStatus wis : WorkItemStatus.values()) {
            map.put(new Integer(wis.getValue()), wis);
        }
    }
    
    public static WorkItemStatus lookup(int value) {
        return map.get(new Integer(value));
    }
}
