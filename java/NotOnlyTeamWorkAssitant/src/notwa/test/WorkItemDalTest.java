package notwa.test;

import notwa.common.ConnectionInfo;
import notwa.wom.*;
import notwa.dal.*;
import notwa.sql.*;
import java.sql.Timestamp;
import java.util.Calendar;
import notwa.logger.LoggingFacade;

public class WorkItemDalTest {
    WorkItemCollection wic;
    WorkItemDal wid;
    ParameterSet pc;
    Timestamp ts;
    ConnectionInfo ci;
    Context context;

    public WorkItemDalTest() {
        try {
            ci = initConnectionInfo();
            context = ContextManager.getInstance().newContext();
            wic = new WorkItemCollection();
            wid = new WorkItemDal(ci, context);
            pc = new ParameterSet(new Parameter(Parameters.WorkItem.ASSIGNED_USER, 1, Sql.Relation.EQUALTY));
            ts = new Timestamp(Calendar.getInstance().getTimeInMillis());

            wic.setCurrentContext(context);
            wid.fill(wic, pc);

            boolean found = false;
            for (WorkItem wi : wic) {
                System.out.println(wi.toString());
                if (wi.getId() < 3) {
                    wi.setExpectedTimestamp(ts);
                }
                if (wi.getId() == 50) {
                    wic.remove(wi);
                    found = true;
                }
            }

            if (!found) {
                WorkItem wi = new WorkItem(50);
                wi.registerWithContext(context);
                wi.setAssignedUser(context.getUser(1));
                wi.setProject(context.getProject(1));
                wi.setDescription("Test!");
                wi.setExpectedTimestamp(ts);
                wi.setNoteCollection(null);
                wi.setParentWorkItem(null);
                wi.setPriority(WorkItemPriority.CRITICAL);
                wi.setStatus(WorkItemStatus.CLOSED);
                wi.setSubject("Testuju ...");
                wic.add(wi);
            }

            wid.update(wic);
        } catch (Exception ex) {
            LoggingFacade.handleException(ex);
        }

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
