/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package su.application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import su.common.SubtitleFactory;
import su.common.Subtitles;

/**
 *
 * @author eTeR
 */
public class Application {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        CommandLine cl = CommandLine.parse(args);
        List<String> lines = new ArrayList<String>();

        BufferedReader reader = null;
        BufferedWriter writer = null;
        try {
            /**
             * Read the input file lines
             */
            reader = new BufferedReader(new FileReader(new File(cl.getSource())));
            while (reader.ready()) {
                lines.add(reader.readLine());
            }

            /**
             * Parse the subtitles and perform the time shift
             */
            Subtitles subs = SubtitleFactory.recognizeSubtitles(cl.getSource());
            subs.parse(lines);
            subs.timeShift(cl.getMilisecondsTimeShift());

            /**
             * Write the subtitles into the output file
             */
            writer = new BufferedWriter(new FileWriter(new File(cl.getDestination())));
            for (String line : subs.formatForFile()) {
                writer.write(line);
                writer.newLine();
            }

        } catch (Exception ex) {
            Logger.getLogger("SubtitleUtils").log(Level.SEVERE, "Error occured while processing the subtitles!", ex);
        } finally {
            try { 
                reader.close();
                writer.close();
            } catch (Exception ex) {}
        }
    }

}
