/*
 * DirectorySynchronizer
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.TreeSet;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class DirectorySynchronizer {

    public DirectorySynchronizer() {

    }

    public DirectoryDiff sync(File src, File dst) throws Exception {
        if (!src.isDirectory() || !dst.isDirectory()) {
            throw new Exception("Either source or destination is not a directory!");
        }

        DirectoryDiff diff = new DirectoryDiff();

        /**
         * Get list of all files on both sides
         */
        TreeSet<ComparableFile> source = listAllFiles(src, src.getAbsolutePath());
        TreeSet<ComparableFile> destination = listAllFiles(dst, dst.getAbsolutePath());

        /**
         * Pull files that remained the same to different collection
         * At the end the source will hold files that are in the source dir but
         * not in the dest dir (to add) and the dest will hold file that are in the
         * dest dir but not in the src (to delete)
         */
        TreeSet<ComparableFile> potentiallyUpdated = new TreeSet<ComparableFile>();
        for (ComparableFile srcFile : source) {
            if (destination.contains(srcFile)) {
                potentiallyUpdated.add(srcFile);
                if (!srcFile.getFile().isDirectory()) {
                    ComparableFile dstFile = destination.subSet(srcFile, true, srcFile, true).first();
                    if (!dstFile.compareToByContent(srcFile)) {
                        diff.update(dstFile.getFile());
                        copyFile(srcFile.getFile().getAbsolutePath(), dstFile.getFile().getAbsolutePath());
                    }
                }
            }
        }

        source.removeAll(potentiallyUpdated); // files to add
        destination.removeAll(potentiallyUpdated); // files to delete

        /**
         * Delete files at first
         */
        for (ComparableFile toDelete : destination) {
            if (!toDelete.getFile().isDirectory()) {
                toDelete.getFile().delete();
                diff.remove(toDelete.getFile());
            }
        }

        /**
         * Then go through the dirs
         */
        for (ComparableFile toDelete : destination) {
            if (toDelete.getFile().isDirectory()) {
                toDelete.getFile().delete();
                diff.remove(toDelete.getFile());
            }
        }

        /**
         * And finaly copy new files
         */
        for (ComparableFile toCopy : source) {
            File copy = null;
            if (toCopy.getFile().isDirectory()) {
                copy = new File(dst.getAbsolutePath().concat(File.separator).concat(toCopy.getRelativePath()));
                copy.mkdir();
            } else {
                copy = new File(dst.getAbsolutePath().concat(File.separator).concat(toCopy.getRelativePath()));
                copyFile(toCopy.getFile().getAbsolutePath(), copy.getAbsolutePath());
            }
            diff.add(copy);
        }

        return diff;
    }

    private void copyFile(String src, String dst) throws Exception {
        File in = new File(src);
        File out = new File(dst);
        boolean success = false;

        try {
            InputStream is = new FileInputStream(in);
            OutputStream os = new FileOutputStream(out);

            try {
                byte[] buf = new byte[1024];
                int len = 0;
                len = is.read(buf);
                while (len > 0) {
                    os.write(buf, 0, len);
                    len = is.read(buf);
                }
                success = true;
            } catch (Exception ex) {
                success = false;
            } finally {
                is.close();
                os.close();
            }
        } catch (Exception ex) {
            success = false;
        }

        if (!success) {
            throw new Exception("Screwed!");
        }
    }

    private TreeSet<ComparableFile> listAllFiles(File file, String root) {
        TreeSet<ComparableFile> files = new TreeSet<ComparableFile>();
        for (File f : file.listFiles()) {
            if (f.isDirectory()) {
                if (!f.getName().equalsIgnoreCase(".svn") && !f.getName().equalsIgnoreCase(".git")) {
                    files.addAll(listAllFiles(f, root));
                    files.add(new ComparableFile(f, f.getAbsolutePath().substring(root.length() + 1)));
                }
            } else {
                files.add(new ComparableFile(f, f.getAbsolutePath().substring(root.length() + 1)));
            }
        }
        return files;
    }

    private class ComparableFile implements Comparable<ComparableFile> {
        private File file;
        private String relativePath;

        public ComparableFile(File file, String relativePath) {
            this.file = file;
            this.relativePath = relativePath;
        }

        public int compareTo(ComparableFile o) {
            return relativePath.compareTo(o.relativePath);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final ComparableFile other = (ComparableFile) obj;
            if ((this.relativePath == null) ? (other.relativePath != null) : !this.relativePath.equals(other.relativePath)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 79 * hash + (this.relativePath != null ? this.relativePath.hashCode() : 0);
            return hash;
        }

        public boolean compareToByContent(ComparableFile cf) {
            boolean equals = true;
            try {
                BufferedReader br1 = new BufferedReader(new FileReader(this.file));
                BufferedReader br2 = new BufferedReader(new FileReader(cf.getFile()));
                try {
                    String line1 = br1.readLine();
                    while (line1 != null) {
                        if (!br2.readLine().equals(line1)) {
                            equals = false;
                            break; // changed line
                        }
                        line1 = br1.readLine();
                    }
                    if (equals && (br2.readLine() != null)) {
                        equals = false; // second file has more lines
                    }
                } catch (Exception ex) {
                    equals = false; // second file is shorter
                } finally {
                    try {
                        br1.close();
                        br2.close();
                    } catch (IOException ioex) {}
                }
            } catch (FileNotFoundException fnfex) {
                equals = false;
            }

            return equals;
        }

        public File getFile() {
            return file;
        }

        public String getRelativePath() {
            return relativePath;
        }

        @Override
        public String toString() {
            return relativePath;
        }

    }

}
