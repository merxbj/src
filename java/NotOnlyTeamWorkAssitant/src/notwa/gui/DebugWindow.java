/*
 * DebugWindow
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

package notwa.gui;

import java.awt.HeadlessException;
import java.util.LinkedList;
import javax.swing.JFrame;
import javax.swing.JTextArea;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class DebugWindow extends JFrame {

    private JTextArea log;
    private LinkedList<String> messages;

    public DebugWindow() throws HeadlessException {
        this.log = new JTextArea();
        this.messages = new LinkedList<String>();

        this.log.setSize(750, 500);

        this.setSize(750,500);
        this.add(log);
    }

    public void appendMessage(String message) {
        messages.push(message);
        log.setText("");
        
        for (String m : messages) {
            log.append(m + "\n");
        }
    }

}
