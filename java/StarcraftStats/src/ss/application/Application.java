/*
 * Application
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

package ss.application;

import ss.data.GameData;
import ss.gui.MainFrame;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class Application {

    public static void main(String[] args) {
        try {
            final CommandLine cl = CommandLine.parse(args);
            final GameData data = new GameData(cl.getDataFilePath());

            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    new MainFrame(data).setVisible(true);
                }
            });

        } catch (InvalidCommandLineException ex) {
            CommandLine.handleInvalidInput(ex);
            CommandLine.showHelp();
        } catch (InvalidDataFileException ex) {
            CommandLine.handleInvalidDataFile(ex);
        } catch (Exception ex) {
            CommandLine.handleException(ex);
        }
    }
}
