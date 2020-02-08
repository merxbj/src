/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package textencodingconverter;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import sun.tools.java.Environment;

/**
 *
 * @author merxbj
 */
public class TextEncodingConverter {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        FileInputStream fis = new FileInputStream(args[0]);
        Scanner s = new Scanner(fis, args[1]);
        List<String> lines = new ArrayList<String>();
        while (s.hasNextLine()) {
            String line = s.nextLine();
            lines.add(line);
        }
        s.close();
        
        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(args[2]), args[3]);
        for (String l : lines) {
            osw.write(l);
            osw.write("\n");
        }
        osw.flush();
        osw.close();
    }
}
