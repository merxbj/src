/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.merxbj.photography;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Matcher;
import javax.swing.filechooser.FileSystemView;

/**
 *
 * @author merxbj
 */
public class JpegCleanup {

    boolean test = false;
    long size = 0L;
    
    public static void main(String[] args) {

        if (!FileSystemView.getFileSystemView().getSystemDisplayName(new File("H:\\")).equals("My Passport (H:)") ||
            !FileSystemView.getFileSystemView().getSystemDisplayName(new File("G:\\")).equals("DATASTORE (G:)")) {
            
            if (!normalizeMounts(args)) {
                throw new RuntimeException("Invalid mounts!");
            }
        }

        String[] roots = args;
        
        for (String root : roots) {
            File f = new File(root);
            if (!f.exists() || !f.isDirectory()) {
                throw new RuntimeException(f.getAbsolutePath());
            }
        }
        
        long totalSize = 0;
        for (String root : roots) {
            File f = new File(root);
            JpegCleanup cln = new JpegCleanup((args.length == 2) && args[1].equalsIgnoreCase("test"));
            cln.handleFile(f);
            System.out.printf("Cleaned up %d MB worth of sidecar JPGs!", cln.size/1024/1024);
            System.out.println("");
            totalSize += cln.size;
        }
        
        System.out.printf("Total: Cleaned up %d MB worth of sidecar JPGs!", totalSize/1024/1024);
        System.out.println("");
    }
    
    private static boolean normalizeMounts(String[] roots) {
        
        String hDriveReplacement = null, gDriveReplacement = null;
        
        for (File f : File.listRoots()) {
            if (FileSystemView.getFileSystemView().getSystemDisplayName(f).startsWith("My Passport")) {
                hDriveReplacement = f.getPath();
            } else if (FileSystemView.getFileSystemView().getSystemDisplayName(f).startsWith("DATASTORE")) {
                gDriveReplacement = f.getPath();
            }
        }
        
        if (hDriveReplacement == null || gDriveReplacement == null) {
            return false;
        } else {
            System.out.println("New H: drive is " + hDriveReplacement);
            System.out.println("New G: drive is " + gDriveReplacement);
        }
        
        for (int i = 0; i < roots.length; i++) {
            String root = roots[i];
            String newRoot = root;

            if (root.startsWith("H:\\")) {
                root = root.replaceFirst("H:\\\\", Matcher.quoteReplacement(hDriveReplacement));
            } else if (root.startsWith("G:\\")) {
                root = root.replaceFirst("G:\\\\", Matcher.quoteReplacement(gDriveReplacement) );
            }

            if (!newRoot.equals(root)) {
                roots[i] = root;
                System.out.printf("Normalized path, oldPath = %s, newPath = %s", root, newRoot);
                System.out.println("");
            }
        }
        
        return true;
    }

    public JpegCleanup(boolean test) {
        this.test = test;
    }
    
    private void handleFile(File file) {
        if (file.isDirectory()) {
            iterateDirectory(file);
        } else {
            File jpeg = new File(file.getPath().replaceAll(".CR2", ".JPG"));
            if (jpeg.exists()) {
                System.out.println("Will delete sidecar JPEG: " + jpeg.getAbsolutePath());
                size += jpeg.length();
                if (!test) {
                    jpeg.delete();
                }
            }
        }
    }

    private void iterateDirectory(File file) {
        File[] files = file.listFiles(new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory() || pathname.getName().endsWith("CR2");
            }
        });
        
        for (File f : files) {
            handleFile(f);
        }
    }

    public long getSize() {
        return size;
    }
}
