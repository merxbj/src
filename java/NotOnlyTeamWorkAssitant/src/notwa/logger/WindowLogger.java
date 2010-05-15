/*
 * WindowLogger
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

package notwa.logger;

import java.util.Observable;
import java.util.Observer;
import notwa.gui.DebugWindow;

/**
 * <code>Logger</code> implementation handling the logging to the GUI window intented
 * for such purposes.
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class WindowLogger extends Logger implements Observer {

    private DebugWindow dw;

    /**
     * The simple constructor just initializing this class.
     */
    public WindowLogger() {
        super();
        init();
    }

    /**
     * The constructor initializing this class and providing the handler for
     * logging exceptions.
     * 
     * @param leh
     */
    public WindowLogger(LoggingExceptionHandler leh) {
        super(leh);
        init();
    }

    /**
     * Initializes the debugging output window.
     */
    private void init() {
        this.dw = new DebugWindow();
        dw.setVisible(true);
    }

    @Override
    public void debug(String message, Object... args) {
        dw.appendMessage(formatMessage(LogLevel.LOG_LEVEL_DEBUG, message, args));
    }

    @Override
    public void error(String message, Object... args) {
        dw.appendMessage(formatMessage(LogLevel.LOG_LEVEL_ERROR, message, args));
    }

    @Override
    public void info(String message, Object... args) {
        dw.appendMessage(formatMessage(LogLevel.LOG_LEVEL_INFO, message, args));
    }

    @Override
    public void update(Observable o, Object arg) {
        LogDispatcher ld = (LogDispatcher) o;
        dw.appendMessage(formatMessage(ld.getLogLevel(), ld.getMessage(), ld.getArgs()));
    }

}
