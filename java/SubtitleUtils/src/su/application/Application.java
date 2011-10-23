/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package su.application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import su.common.ParseParameters;
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

        FileInputStream in = null;
        FileOutputStream out = null;
        try {
            
            /**
             * Get the input stream from the file
             */
            Map<String, Object> parseParameters = new HashMap<String, Object>();
            parseParameters.put(ParseParameters.INPUT_ENCODING, cl.getInputEncoding());
            in = new FileInputStream(new File(cl.getSource()));
            
            /**
             * Parse the subtitles and perform the time shift
             */
            Subtitles subs = SubtitleFactory.recognizeSubtitles(cl.getSource());
            subs.parseFromStream(in, parseParameters);
            subs.timeShift(cl.getMilisecondsTimeShift(), cl.getMilisecondsOffset());

            /**
             * Get the output stream to the file
             */
            Map<String, Object> outputParameters = new HashMap<String, Object>();
            outputParameters.put(ParseParameters.OUTPUT_ENCODING, cl.getOutputEncoding());
            out = new FileOutputStream(new File(cl.getDestination()));
            
            /**
             * Write the subtitles back to the file
             */
            subs.writeToStream(out, outputParameters);
            

        } catch (Exception ex) {
            Logger.getLogger("SubtitleUtils").log(Level.SEVERE, "Error occured while processing the subtitles!", ex);
        } finally {
            try { 
                in.close();
                out.close();
            } catch (Exception ex) {}
        }
    }

}
