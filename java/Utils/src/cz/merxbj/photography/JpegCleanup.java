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
        String root = args[0];
        File f = new File(root);
        JpegCleanup cln = new JpegCleanup((args.length == 2) && args[1].equalsIgnoreCase("test"));
        cln.handleFile(f);
        System.out.printf("Cleaned up %d MB worth of sidecar JPGs!", cln.size/1024/1024);
        System.out.println("");
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
