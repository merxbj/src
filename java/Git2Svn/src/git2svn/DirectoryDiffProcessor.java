/*
 * DirectoryDiffProcessor
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
import java.util.Collection;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class DirectoryDiffProcessor {

    public void applyChanges(DirectoryDiff diff, Updatable u) throws Exception {
        
        processUpdates(diff.getUpdated(), u);
        processRemovals(diff.getRemoved(), u);
        processAdditions(diff.getAdded(), u);
        
    }

    /**
     * Process file updates
     * @param files
     * @param updatable
     */
    private void processUpdates(Collection<File> files, Updatable updatable) throws Exception {
        for (File updated : files) {
            log(updatable.update(updated));
        }
    }

    /**
     * Process removals
     * Svn removes all childs of removed folder so make sure that only top
     * level objects are being manipulated.
     *
     * Pick all removed dirs
     */
    private void processRemovals(Collection<File> files, Updatable updatable) throws Exception {

        ArrayList<File> dirs = new ArrayList<File>();
        for (File f : files) {
            if (f.isDirectory()) {
                dirs.add(f);
            }
        }

        /**
         * Remove child objects of directories from the list to be deleted
         */
        ArrayList<File> notNeccessarry = new ArrayList<File>();
        for (File dir : dirs) {
            for (File file : files) {
                if (!file.equals(dir) && file.getAbsolutePath().startsWith(dir.getAbsolutePath())) {
                    notNeccessarry.add(file);
                }
            }
            files.removeAll(notNeccessarry);
        }

        /**
         * Finally process actual deletions
         */
        for (File removed : files) {
            log(updatable.remove(removed));
        }
    }

    /**
     * Process additions
     * Svn adds all childs of added folder so make sure that only top
     * level objects are being manipulated.
     *
     * Pick all added dirs
     */
    private void processAdditions(Collection<File> files, Updatable updatable) throws Exception {
        ArrayList<File> dirs = new ArrayList<File>();
        for (File f : files) {
            if (f.isDirectory()) {
                dirs.add(f);
            }
        }

        /**
         * Remove child objects of directories from the list to be added
         */
        ArrayList<File> notNeccessarry = new ArrayList<File>();
        for (File dir : dirs) {
            for (File file : files) {
                if (!file.equals(dir) && file.getAbsolutePath().startsWith(dir.getAbsolutePath())) {
                    notNeccessarry.add(file);
                }
            }
            files.removeAll(notNeccessarry);
        }

        /**
         * Finally process actual additions
         */
        for (File added : files) {
            log(updatable.add(added));
        }
    }

    private static void log(ArrayList<String> lines) {
        for (String line : lines) {
            System.out.println(line);
        }
    }

}
