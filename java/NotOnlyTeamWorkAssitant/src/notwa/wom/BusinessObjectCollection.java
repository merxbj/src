package notwa.wom;

import java.util.*;
import notwa.exception.ContextException;

public abstract class BusinessObjectCollection<T extends BusinessObject> implements Iterable<T> {

    protected ArrayList<T> collection = new ArrayList<T>();
    protected Context currentContext;

    @Override
    public Iterator<T> iterator() {
        return collection.iterator();
    }
    
    public boolean add(T bo) throws ContextException {
        if ((bo.getCurrentContext() != null) && (this.getCurrentContext() != null) &&
                bo.getCurrentContext().equals(this.getCurrentContext())) {
            if (collection.add(bo)) {
                bo.attach(this);
                return true;
            } else {
                return false;
            }
        } else {
            throw new ContextException("BusinessObject lives in another context than BusinessObjectCollection!");
        }
    }
    
    public boolean remove(T bo) throws ContextException {
        if ((bo.getCurrentContext() != null) && (this.getCurrentContext() != null) &&
                bo.getCurrentContext().equals(this.getCurrentContext())) {
            if (collection.remove(bo)) {
                bo.detach();
                return true;
            } else {
                return false;
            }
        } else {
            throw new ContextException("BusinessObject lives in another context than BusinessObjectCollection!");
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
