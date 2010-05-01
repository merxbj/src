/*
 * BusinessObjectsTest
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
import notwa.wom.*;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class BusinessObjectsTest {

    public BusinessObjectsTest() {
        Context ctx = ContextManager.getInstance().newContext();
        WorkItem wi1 = new WorkItem(10);
        WorkItem wi2 = new WorkItem(1000000);
        WorkItem wi3 = new WorkItem(1);
        WorkItemCollection wic = new WorkItemCollection(ctx);
        wi1.registerWithContext(ctx);
        wi2.registerWithContext(ctx);
        wi3.registerWithContext(ctx);
        try {
            wic.add(wi3);
            wic.add(wi2);
            wic.add(wi1);

            WorkItem wi = wic.getByPrimaryKey(10);
        } catch (Exception ex) {System.out.println(ex.toString());}


        if (wi1.equals(wi2)) {
            System.out.println("Jsou stejny!");
        }

        System.out.println(wi1.compareTo(wi2));
    }

}
