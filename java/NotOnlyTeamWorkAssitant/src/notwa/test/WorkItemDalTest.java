/*
 * Test
 *
 * Copyright (C) 2010  Jaroslav Merxbauer
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package notwa.test;

import notwa.common.ConnectionInfo;
import notwa.wom.*;
import notwa.dal.*;
import notwa.sql.*;
import java.sql.Timestamp;
import java.util.Calendar;
import notwa.logger.LoggingFacade;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class WorkItemDalTest {
    WorkItemCollection wic;
    WorkItemDal wid;
    ParameterSet pc;
    Timestamp ts;
    ConnectionInfo ci;
    Context context;

    /**
     * 
     */
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
        ConnectionInfo conInfo = new ConnectionInfo();
        conInfo.setDbname("notwa");
        conInfo.setHost("213.192.44.108");
        conInfo.setPort("9970");
        conInfo.setLabel("INTEGRI MYSQL");
        conInfo.setUser("merxbj");
        conInfo.setPassword("ahoj");
        return conInfo;
    }

}
