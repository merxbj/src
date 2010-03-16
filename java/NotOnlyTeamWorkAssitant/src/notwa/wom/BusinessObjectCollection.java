package notwa.wom;

import java.util.*;

public abstract class BusinessObjectCollection implements Iterable<BusinessObject> {

    protected ArrayList<BusinessObject> collection = new ArrayList<BusinessObject>();

    public Iterator<BusinessObject> iterator() {
        return collection.iterator();
    }
    
    public void add(BusinessObject bo) {
        collection.add(bo);
        bo.attach(this);
    }
    
    public void remove(BusinessObject bo) {
        collection.remove(bo);
        bo.detach();
    }
    
    public int size() {
        return collection.size();
    }
    
    public BusinessObject get(int index) {
        return collection.get(index);
    }
}
