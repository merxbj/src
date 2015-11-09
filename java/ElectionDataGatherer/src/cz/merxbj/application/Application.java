/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.merxbj.application;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 * @author jm185267
 */
public class Application {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws MalformedURLException {
        String outputDirName = "c:\\temp\\elections";
        File outputDir = new File(outputDirName);
        outputDir.mkdirs();

        URL url = new URL("http://volby.cz/pls/kv2014/vysledky_obec?datumvoleb=20150131&cislo_obce=562971");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");

        Object sleeper = new Object();
        boolean forever = true;
        while (forever) {
            
            try {
            
                Calendar car = Calendar.getInstance();
                String timestamp = sdf.format(car.getTime());
                String fileName = String.format("%s\\SnapshotAt%s.xml", outputDirName, timestamp);

                System.out.println("About to store an elections snapshot into " + fileName);

                try (FileOutputStream fos = new FileOutputStream(fileName)) {
                    byte buffer[] = new byte[4096];
                    InputStream is = url.openStream();
                    int readBytes = is.read(buffer, 0, 4096);
                    while ((readBytes > 0)) {
                        fos.write(buffer, 0, readBytes);
                        readBytes = is.read(buffer);
                    }
                    System.out.println("Elections snapshot has been written successfully!");
                }
            } catch (Exception ex) {
                System.out.println(ex.toString());
            }
            
            try {
                synchronized (sleeper) {
                    System.out.println("ElectionDataGatherer goes to sleep...");
                    sleeper.wait(60*1000L);
                    System.out.println("ElectionDataGatherer wakes up!");
                }
            } catch (InterruptedException iex) {
                System.out.println(iex.toString());
                forever = false;
            }
        }
    }
    
}
