/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.merxbj.photography;

import java.io.File;
import java.io.FileFilter;

/**
 *
 * @author merxbj
 */
public class JpegCleanup {
    
    boolean test = false;
    long size = 0L;
    
    public static void main(String[] args) {
        String[] roots = new String[] {
            "/Users/merxbj/Pictures/2015", 
            "/Users/merxbj/Pictures/2016",
            "/Volumes/My Passport/Jarda Backup/2015",
            "/Volumes/My Passport/Jarda Backup/2016",
            "/Volumes/DATASTORE/Production"
        };
        
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
