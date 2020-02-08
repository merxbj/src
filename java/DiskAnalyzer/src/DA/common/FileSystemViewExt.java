/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DA.common;

import java.awt.Frame;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileSystemView;

/**
 *
 * @author mrneo
 */
public class FileSystemViewExt extends FileSystemView {

    @Override
    public File createNewFolder(File containingDir) throws IOException {
        /* Unnessesary */
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public File[] getRoots() {
        String osName = System.getProperty("os.name");

        if (osName.equals("Linux")) {
            return this.getLinuxRoots();
        }
        else if (osName.startsWith("Windows")) {
            return File.listRoots();
        }
        else {
            JOptionPane.showMessageDialog(new Frame(), "You have unsupported operating system, sorry.", "Error message", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    private File[] getLinuxRoots() {
        File mtabFile = new File("/etc/mtab");
        File[] roots = new File[50];
        int rootsCount = 0;
        Scanner fileScanner = null;

        try {
            fileScanner = new Scanner(new FileReader(mtabFile));
            
            while (fileScanner.hasNextLine()) {
                Scanner lineScanner = new Scanner(fileScanner.nextLine());
                lineScanner.useDelimiter(" ");
                if (lineScanner.hasNext()) {
                    String device = lineScanner.next();
                    String mountPoint = lineScanner.next();
                    
                    if (device.startsWith("/dev/")) {
                        roots[rootsCount++] = new File(mountPoint);
                    }
                }
            }
            
            return clearArray(roots);
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(new Frame(), "An error occured while reading /etc/mtab.\n" + e , "Error message", JOptionPane.ERROR_MESSAGE);
            fileScanner.close();
            return null;
        }
    }
    
    private File[] clearArray(File[] uncleaned) {
        int counter = 0;
        for (File record : uncleaned) {
            if (record != null) {
                counter++;
            }
        }
        
        File[] cleaned = Arrays.copyOf(uncleaned, counter);
        return cleaned;
    }
}
