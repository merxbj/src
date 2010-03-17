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
    ConnectionInfo ci;

    public WorkItemDalTest() {
        ci = initConnectionInfo();
        wic = new WorkItemCollection();
        wid = new WorkItemDal(ci);
        pc = new ParameterCollection(new Parameter [] {new Parameter(Parameters.WorkItem.ASSIGNED_USER, 1, Sql.Condition.EQUALTY)});
    }
    
    public void test() {
        wic.setCurrentContext(ContextManager.getInstance().newContext());
        wid.Fill(wic, pc);
    }

    private ConnectionInfo initConnectionInfo() {
        ConnectionInfo ci = new ConnectionInfo();
        ci.setDbname("notwa");
        ci.setHost("213.192.44.108");
        ci.setPort("9970");
        ci.setLabel("INTEGRI MYSQL");
        ci.setUser("merxbj");
        ci.setPassword("ahoj");
        return ci;
    }

}
