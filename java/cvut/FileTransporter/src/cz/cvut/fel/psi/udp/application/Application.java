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
package cz.cvut.fel.psi.udp.application;

import cz.cvut.fel.psi.udp.core.FileTransporter;

public final class Application {

    private Application() {
    }

    public static void main(final String[] args) {

        CommandLine cl = CommandLine.parse(args);
        if (cl == null) {
            CommandLine.printUsage();
            return;
        }
        
        ProgressLoggerFactory.setLogLevel(cl.getLogLevel());

        FileTransporter transporter = new FileTransporter();

        if (cl.getFirmwareFileName().equals("")) {
            transporter.download(cl.getHostname(), cl.getPort(), cl.getDownloadFileName());
        } else {
            transporter.upload(cl.getHostname(), cl.getPort(), cl.getFirmwareFileName());
        }

    }
}
