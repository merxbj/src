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
    
    private static final int SERVER_PORT = 3999;
    private static final String LOCAL_FILE_NAME = "foto.png";
    
    public static void main(String[] args) {
        
        CommandLine cl = CommandLine.parse(args);
        FileTransporter transporter = new FileTransporter(new SimpleProgressSink());
        
        if (cl.getFirmwareFileName().equals("")) {
            transporter.download(cl.getHostname(), SERVER_PORT, LOCAL_FILE_NAME);
        } else {
            transporter.upload(cl.getHostname(), SERVER_PORT, cl.getFirmwareFileName());
        }
                
    }  
}
