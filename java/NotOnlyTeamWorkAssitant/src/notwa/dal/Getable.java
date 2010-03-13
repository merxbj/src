package notwa.dal;

import notwa.sql.ParameterCollection;
import notwa.wom.BusinessObject;

public interface Getable<T extends BusinessObject> {
        T get(ParameterCollection primaryKey);
}
