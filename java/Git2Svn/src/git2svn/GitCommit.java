/*
 * GitCommit
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

import java.util.ArrayList;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class GitCommit {

    private String sha1;
    private ArrayList<String> messageLines = new ArrayList<String>();

    public GitCommit(ArrayList<String> logChunk) {
        sha1 = logChunk.get(0).substring(logChunk.get(0).indexOf(" ") + 1);
        int i = 1;
        while ((i < logChunk.size()) && !logChunk.get(i).startsWith("Date:")) {
            i++; // the commit message start after blank line after date
        }
        messageLines.addAll(logChunk.subList(i + 2, logChunk.size() - 1));
    }

    public ArrayList<String> getMessageLines() {
        return messageLines;
    }

    public String getSha1() {
        return sha1;
    }

    @Override
    public String toString() {
        return String.format("%s: %s", sha1, messageLines.get(0));
    }

}
