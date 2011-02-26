/*
 * MyOwnAppender
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

package log4jlogger;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;

/**
 *
 * @author eTeR
 * @version %I% %G%
 */
public class MyOwnAppender extends AppenderSkeleton {

    @Override
    protected void append(LoggingEvent event) {
        System.out.println(String.format("MyOwnAppender: %s", layout.format(event)));
        if (layout.ignoresThrowable()) {
            String[] readableThrowable = event.getThrowableStrRep();
            if (readableThrowable != null) {
                for (String line : readableThrowable) {
                    System.out.print(line);
                    System.out.println(Layout.LINE_SEP);
                }
            }
        }
    }

    @Override
    public void close() {
        
    }

    @Override
    public boolean requiresLayout() {
        return true;
    }

}
