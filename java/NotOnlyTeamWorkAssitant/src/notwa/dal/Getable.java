package notwa.dal;

import notwa.sql.ParameterSet;
import notwa.wom.BusinessObject;
import notwa.exception.DalException;

public interface Getable<T extends BusinessObject> {
        T get(ParameterSet primaryKey) throws DalException;
}
