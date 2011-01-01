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

package psitp4.application;

import psitp4.core.FileTransporter;

/**
 *
 * @author Jaroslav Merxbauer
 */
public class Application {
    
    public static void main(String[] args) {
        CommandLine cl = CommandLine.parse(args);
        FileTransporter transporter = new FileTransporter();
        long size = 0;

        try {
            size = transporter.transfer(cl.getHostname(), cl.getPort(), cl.getRemoteFileName(), cl.getLocalFileName());
        } catch (Exception ex) {
            handleException(ex);
        }

        reportSuccess(size, cl);
    }

    private static void reportSuccess(long size, CommandLine cl) {
        String message = String.format("Successfully transfered %d of bytes!", size);
        System.out.println(message);
    }

    private static void handleException(Exception ex) {
        System.out.println(ex.toString());
    }
    
}
