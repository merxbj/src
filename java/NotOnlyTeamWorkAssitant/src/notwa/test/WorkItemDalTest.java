package notwa.test;

import notwa.common.ConnectionInfo;
import notwa.wom.*;
import notwa.dal.*;
import notwa.sql.*;
import java.sql.Timestamp;
import java.util.Calendar;

public class WorkItemDalTest {
    WorkItemCollection wic;
    WorkItemDal wid;
    ParameterCollection pc;
    Timestamp ts;

    public WorkItemDalTest() {
        wic = new WorkItemCollection();
        wid = new WorkItemDal(new ConnectionInfo());
        pc = new ParameterCollection(new Parameter [] {new Parameter(Parameters.WorkItem.ASSIGNED_USER, 15, Sql.Condition.EQUALTY),
                                                       new Parameter(Parameters.WorkItem.DEADLINE, new Timestamp(Calendar.getInstance().getTimeInMillis()), Sql.Condition.GREATER)});
    }

    public void test() {
        wid.Fill(wic, pc);
    }
}
