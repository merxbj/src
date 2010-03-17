package notwa.wom;

import java.util.*;

public abstract class BusinessObjectCollection<T extends BusinessObject> implements Iterable<T> {

    protected ArrayList<T> collection = new ArrayList<T>();

    @Override
    public Iterator<T> iterator() {
        return collection.iterator();
    }
    
    public boolean add(T bo) {
        if (collection.add(bo)) {
            bo.attach(this);
            return true;
        } else {
            return false;
        }
    }
    
    public boolean remove(T bo) {
        if (collection.remove(bo)) {
            bo.detach();
            return true;
        } else {
            return false;
        }
    }
    
    public int size() {
        return collection.size();
    }
    
    public T get(int index) {
        return (T) collection.get(index);
    }
}
