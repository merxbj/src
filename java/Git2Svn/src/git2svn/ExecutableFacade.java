/*
 * ExecutableFacade
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

package git2svn;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public abstract class ExecutableFacade {

    protected File workingDir;
    protected String executable;

    protected ArrayList<String> executable(String command, String ... args) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(buildCommand(command, args));
        pb.directory(workingDir);

        Process p = pb.start();

        return readFromStdout(p);
    }

    private ArrayList<String> readFromStdout(Process p) throws Exception {
        BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
        BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        ArrayList<String> stdout = new ArrayList<String>();

        String line = input.readLine();
        while (line != null) {
            stdout.add(line);
            line = input.readLine();
        }
        input.close();
        if (stdout.size() > 0) {
            return stdout;
        }

        line = error.readLine();
        while (line != null) {
            stdout.add(line);
            line = error.readLine();
        }
        error.close();
        if (stdout.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (String errline : stdout) {
                sb.append(String.format("%s\n", errline));
            }
        }

        return stdout;
    }

    private List<String> buildCommand(String cmd, String ... args) {
        List<String> command = new ArrayList<String>();
        command.add(executable);
        command.add(cmd);
        for (String arg : args) {
            command.add(arg);
        }
        return command;
    }
}
