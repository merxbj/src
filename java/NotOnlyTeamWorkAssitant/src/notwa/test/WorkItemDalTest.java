package notwa.test;

import notwa.common.ConnectionInfo;
import notwa.wom.*;
import notwa.dal.*;
import notwa.sql.*;

public class WorkItemDalTest {
    WorkItemCollection wic;
    WorkItemDal wid;
    ParameterCollection pc;

    public WorkItemDalTest() {
        wic = new WorkItemCollection();
        wid = new WorkItemDal(new ConnectionInfo());
        pc = new ParameterCollection(new Parameter [] {new Parameter(Parameters.User.ID, 15, Sql.Condition.EQUALTY),
                                                       new Parameter(Parameters.Project.ID, 11, Sql.Condition.EQUALTY)});
    }

    public void test() {
        wid.Fill(wic, pc);
    }
}
