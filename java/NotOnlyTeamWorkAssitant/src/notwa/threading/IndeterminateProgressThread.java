/*
 * IndeterminateProgressThread
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

package notwa.threading;

import notwa.logger.LoggingFacade;
import notwa.gui.JStatusBar;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class IndeterminateProgressThread {
    Action action;
    WorkerThread worker;

    public IndeterminateProgressThread(Action action) {
        this.action = action;
        this.worker = new WorkerThread(action, new TerminateCallback() {
            @Override
            public void onTerminate() {
                JStatusBar.getInstance().endAnimate();
            }
        });
    }

    public void run() {
        JStatusBar.getInstance().beginAnimate();
        worker.start();
    }

    private class WorkerThread extends Thread {

        Action action;
        TerminateCallback callback;

        public WorkerThread(Action action, TerminateCallback callback) {
            super();
            this.action = action;
            this.callback = callback;
        }

        @Override
        public void run() {
            try {
                action.perform();
            } catch (Exception ex) {
                LoggingFacade.handleException(ex);
            } finally {
                callback.onTerminate();
            }
        }
    }

}
