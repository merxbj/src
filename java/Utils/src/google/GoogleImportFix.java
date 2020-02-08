package google;

import java.io.*;
import java.util.*;

public class GoogleImportFix {
    public static void main(String[] args) {
        String path = "c:/temp/contacts.csv";
        File csv = new File(path + ".old");

        BufferedReader bfr = null;
        ArrayList<ArrayList<String>> table = new ArrayList<ArrayList<String>>();
        try {
            bfr = new BufferedReader(new FileReader(csv));
            String line = null;
            while ((line = bfr.readLine()) != null) {
                String [] cols = line.split(",");
                ArrayList<String> row = new ArrayList<String>();
                for (String col : cols) {
                    row.add(col);
                }
                table.add(row);
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
        } finally {
            try {
                bfr.close();
            } catch (Exception ex) {
            }
        }

        for (ArrayList<String> row : table) {
            // set Middle name to LastName, FirstName everywhere
            if (table.indexOf(row) > 0) {
                row.set(1, String.format("\"%s, %s\"", row.get(2), row.get(0)));
            }
        }

        File csvo = new File(path);
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(csvo));
            for (ArrayList<String> row : table) {
                StringBuilder sb = new StringBuilder();
                for (String col : row) {
                    sb.append(col);
                    sb.append(",");
                }
                String line = sb.toString().substring(0, sb.length() - 2); // drop the ending comma
                bw.append(line);
                bw.newLine();
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
        } finally {
            try {
                bw.close();
            } catch (Exception ex) {

            }
        }
    }
}
