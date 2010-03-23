package notwa.dal;

import notwa.sql.ParameterSet;
import notwa.wom.BusinessObjectCollection;

public interface Fillable<T extends BusinessObjectCollection> {
    int Fill(T boc);
    int Fill(T boc, ParameterSet pc);
}
