/*
 * GitFacade
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

import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class GitFacade extends ExecutableFacade {

    public GitFacade(String git, String workingDir) {
        this.executable = git;
        this.workingDir = new File(workingDir);
    }

    public ArrayList<String> log(String ... args) throws Exception {
        return executable("log", args);
    }

    public ArrayList<String> checkout(String ... args) throws Exception {
        return executable("checkout", args);
    }

    public ArrayList<String> clean(String ... args) throws Exception {
        return executable("clean", args);
    }
    
}
