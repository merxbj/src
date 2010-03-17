package notwa.dal;

import notwa.sql.ParameterCollection;
import notwa.wom.BusinessObject;
import notwa.exception.DalException;

public interface Getable<T extends BusinessObject> {
        T get(ParameterCollection primaryKey) throws DalException;
}
