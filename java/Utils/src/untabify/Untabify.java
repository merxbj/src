package untabify;

import java.io.*;
import java.util.*;

public class Untabify {

    public static void main(String[] args) {
        File dir = new File(args[0]);
        FilenameFilter filter = new FilenameFilter() {

            public boolean accept(File dir, String name) {
                return name.endsWith(".java");
            }
        };

        File[] subdirs = dir.listFiles();
        for (File sd : subdirs) {
            File[] jss = sd.listFiles(filter);
            for (File js : jss) {
                untabifyFile(js);
            }
        }
    }

    public static void untabifyFile(File js) {
        ArrayList<String> lines = new ArrayList<String>();
        String line = null;

        try {
            BufferedReader br = new BufferedReader(new FileReader(js));
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
            br.close();
            ArrayList<String> untabified = untabify(lines);

            BufferedWriter bw = new BufferedWriter(new FileWriter(js, false));
            for (String untabifiedLine : untabified) {
                bw.write(untabifiedLine);
                bw.newLine();
            }
            bw.close();
        } catch (Exception ex) {
            System.out.println("Exception!: " + ex.toString());
        }
    }

    public static ArrayList<String> untabify(ArrayList<String> tabbed) {
        ArrayList<String> untabified = new ArrayList<String>();
        for (String line : tabbed) {
            line = line.replaceAll("\t", "    ");
            untabified.add(line);
        }
        return untabified;
    }

}
