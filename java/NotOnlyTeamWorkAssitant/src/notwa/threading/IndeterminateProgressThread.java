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

/**
 * Intention of this <code>class</code> is to provide a unified way to run some
 * action in a bacground thread which progress is being considered as indeterminate
 * and this fact is expressed by the element implemnting an {@link IndeterminablePrgoressExpressioner}
 * interface which actually means that this elemnt is capabale of expressing of
 * a such progress state.
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class IndeterminateProgressThread {
    
    /**
     * The action to be run in a background.
     */
    protected Action action;

    /**
     * The background thread to hold the action.
     */
    protected WorkerThread worker;

    /**
     * The element to express the indeterminate progress.
     */
    protected IndeterminablePrgoressExpressioner progress;

    /**
     * The sole constructor accepting the action to be performed and the element
     * to express the progress.
     *
     * @param action The action.
     * @param ipe The expressioner.
     */
    public IndeterminateProgressThread(Action action, IndeterminablePrgoressExpressioner ipe) {
        this.action = action;
        this.progress = ipe;
        this.worker = new WorkerThread(action, new Action() {
            @Override
            public void perform() {
                if (progress != null) {
                    progress.endIndetermination();
                }
            }
        });
    }

    /**
     * Begins the execution of the action.
     */
    public void run() {
        if (progress != null) {
            progress.beginIndetermination();
        }
        worker.start();
    }

    public void join() {
        try {
            worker.join();
        } catch (InterruptedException iex) {
            
        }
    }

    /**
     * The bacground thread implementation.
     */
    protected class WorkerThread extends Thread {

        /**
         * The action to be run in a background.
         */
        protected Action onRun;

        /**
         * The action to be done upon this worker thread termination.
         */
        protected Action onTerminate;

        /**
         * The sole constructor of the background thread accepting two actions:
         * <ul>
         * <li>The action to be run in a background.</li>
         * <li>The action to be done upon this worker thread termination.</li>
         * </ul>
         *
         * @param onRun The action to be run in a background.
         * @param onTerminate The action to be done upon this worker thread termination.
         */
        public WorkerThread(Action onRun, Action onTerminate) {
            super();
            this.onRun = onRun;
            this.onTerminate = onTerminate;
        }

        @Override
        public void run() {
            try {
                onRun.perform();
            } catch (Exception ex) {
                LoggingFacade.handleException(ex);
            } finally {
                onTerminate.perform();
            }
        }
    }

}
