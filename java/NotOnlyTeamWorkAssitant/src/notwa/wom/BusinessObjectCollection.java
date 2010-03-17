package notwa.wom;

import java.util.*;

public abstract class BusinessObjectCollection<T extends BusinessObject> implements Iterable<T> {

    protected ArrayList<T> collection = new ArrayList<T>();
    protected Context currentContext;

    @Override
    public Iterator<T> iterator() {
        return collection.iterator();
    }
    
    public boolean add(T bo) {
        if (bo.getCurrentContext().equals(bo.getCurrentContext())) {
            if (collection.add(bo)) {
                bo.attach(this);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    
    public boolean remove(T bo) {
        if (bo.getCurrentContext().equals(bo.getCurrentContext())) {
            if (collection.remove(bo)) {
                bo.detach();
                return true;
            } else {
                return false;
            }
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

    public Context getCurrentContext() {
        return currentContext;
    }

    public void setCurrentContext(Context currentContext) {
        this.currentContext = currentContext;
    }
}
