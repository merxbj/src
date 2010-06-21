/*
 * Main
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
import java.util.Collections;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class Main {

    private static final String git = "c:/Program Files (x86)/Git/bin/git.exe";
    private static final String svn = "svn";
    private static final String src = "d:/repos/srcsvn/java/NotOnlyTeamWorkAssitant/";
    private static final String dest = "d:/repos/svn/";

    private static SvnFacade sf = new SvnFacade(svn, dest);
    private static GitFacade gf = new GitFacade(git, src);
    private static DirectorySynchronizer ds = new DirectorySynchronizer();
    private static DirectoryDiffProcessor ddp = new DirectoryDiffProcessor();

    public static void main(String[] args) {

        ArrayList<GitCommit> commits = new ArrayList<GitCommit>();
        ArrayList<String> logChunk = new ArrayList<String>();

        try {

            /**
             * git log
             */
            ArrayList<String> stdout = gf.log();

            /**
             * Parse stdout
             */
            boolean first = true;
            for (String line : stdout) {
                if (!first && line.startsWith("commit")) {
                    GitCommit gc = new GitCommit(logChunk);
                    commits.add(gc);
                    logChunk.clear();
                }
                if (first) {
                    first = false;
                }
                logChunk.add(line);
            }
            stdout = null;

            /**
             * Remove unwanted commits
             */
            ArrayList<GitCommit> garbage = new ArrayList<GitCommit>();
            for (GitCommit gc : commits) {
                String title = (gc.getMessageLines().size() > 0) ? gc.getMessageLines().get(0) : "";
                if (!title.trim().startsWith("NOTWA")) {
                    garbage.add(gc);
                }
            }
            commits.removeAll(garbage);

            /**
             * Transport git repo to svn repo
             * We want to go from the oldes history to the newest (surprising!)
             */
            Collections.reverse(commits);
            for (GitCommit gc : commits) {
                /**
                 * Be verboose a little!
                 */
                System.out.println(String.format("Processing: ", gc));

                /**
                 * Checkout commit to be ready in git repository directory
                 */
                log(gf.checkout(gc.getSha1()));
                System.out.println("\t+ Checked out");

                /**
                 * Sync the git repo dir and svn repo dir and retrieve the directory diff
                 */
                DirectoryDiff diff = ds.sync(new File(src), new File(dest));
                System.out.println("\t+ Synchronized");

                /**
                 * Promote the changes found by the synchronizer to the svn repo
                 */
                ddp.applyChanges(diff, sf);
                System.out.println("\t+ Changes applied to svn");

                /**
                 * Commit the changes
                 */
                log(sf.commit(gc));
                System.out.println("\t+ Aplied changes commited to svn repo");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void log(ArrayList<String> lines) {
        for (String line : lines) {
            System.out.println(line);
        }
    }

}
