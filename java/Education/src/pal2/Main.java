package pal2;

/*
 * Client
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

import java.net.*;
import java.io.*;

/**
 *
 * @author Jiri Fric
 * @version %I% %G%
 */
public class Main {

    public static void main(String[] args) {

        try {

            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            while (reader.ready()) {
                builder.append(reader.readLine()).append("\n");
            }

            Socket sock = new Socket("89.102.55.136", 80);

            DataOutputStream out = new DataOutputStream(sock.getOutputStream());
            out.writeUTF(builder.toString());

            sock.close();

        } catch (Exception e) {
            System.out.println("ERROR");
        }

        System.out.println("ERROR");

    }

}
