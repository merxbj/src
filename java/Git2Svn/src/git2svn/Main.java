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

    private static final String gitPath = "c:/Program Files (x86)/Git/bin/git.exe";
    private static final String svnPath = "svn";
    private static final String srcPath = "d:/repos/srcsvn/java/NotOnlyTeamWorkAssitant/";
    private static final String destPath = "d:/repos/svn/";

    private static SvnFacade svn = new SvnFacade(svnPath, destPath);
    private static GitFacade git = new GitFacade(gitPath, srcPath);
    private static DirectorySynchronizer synchronizer = new DirectorySynchronizer();
    private static DirectoryDiffProcessor diffProcessor = new DirectoryDiffProcessor();

    public static void main(String[] args) {

        ArrayList<GitCommit> commits = new ArrayList<GitCommit>();
        ArrayList<String> logChunk = new ArrayList<String>();

        try {

            /**
             * git log
             */
            ArrayList<String> stdout = git.log();

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
             * Remove unwanted commits regarding another projects
             */
            ArrayList<GitCommit> garbage = new ArrayList<GitCommit>();

            for (GitCommit gc : commits) {
                String title = (gc.getMessageLines().size() > 0) ? gc.getMessageLines().get(0) : "";
                if (!title.trim().startsWith("NOTWA")) {
                    garbage.add(gc);
                }
            }

            /**
             * Having the collection sorted chronologicaly from the oldest to newest
             * is the expected situtation for the following code
             */
            Collections.reverse(commits);

            /**
             * Remove commits with broken notwa.sql file
             */
            boolean isGarbage = false;
            for (GitCommit gc : commits) {
                if (gc.getSha1().equals("161ea8c8a38480e1759ce504496bfe7aaea421ad")) {
                    isGarbage = true;
                    
                } 
                if (isGarbage) {
                    garbage.add(gc);
                }
                if (isGarbage && gc.getSha1().equals("2f107903d1012aa55bb2253da208e733238077b2")) {
                    break;
                }
            }

            commits.removeAll(garbage);

            /**
             * Transport git repo to svn repo
             */
            for (GitCommit gc : commits) {
                /**
                 * Be verboose a little!
                 */
                System.out.println(String.format("Processing: %s", gc));

                /**
                 * Clean the git repo
                 */
                log(git.checkout("*"));
                log(git.clean("-f"));
                System.out.println("\t+ Cleaned the git repo");

                /**
                 * Checkout commit to be ready in git repository directory
                 */
                log(git.checkout(gc.getSha1()));
                System.out.println("\t+ Checked out");

                /**
                 * Sync the git repo dir and svn repo dir and retrieve the directory diff
                 */
                DirectoryDiff diff = synchronizer.sync(new File(srcPath), new File(destPath));
                System.out.println("\t+ Synchronized");

                /**
                 * Promote the changes found by the synchronizer to the svn repo
                 */
                diffProcessor.applyChanges(diff, svn);
                System.out.println("\t+ Changes applied to svn");

                /**
                 * Commit the changes
                 */
                log(svn.commit(gc));
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
