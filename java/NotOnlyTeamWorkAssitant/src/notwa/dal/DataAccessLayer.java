package notwa.dal;

import notwa.wom.BusinessObjectCollection;
import notwa.wom.BusinessObject;

public interface DataAccessLayer {
	int Fill(BusinessObjectCollection boc);
	int Fill(BusinessObject boc, ParameterCollection pc);
}
