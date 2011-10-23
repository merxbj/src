/*
 * Subtitles
 *
 * Copyright (C) 2010  Jaroslav Merxbauer
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package su.srt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import su.common.ParseParameters;
import su.common.SubtitleFormatException;
import su.common.Subtitles;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class SrtSubtitles implements Subtitles, FileRepresentable {
    private List<Subtitle> subtitles;

    public SrtSubtitles() {
        subtitles = new ArrayList<Subtitle>();
    }

    List<Subtitle> getSubtitles() {
        return subtitles;
    }

    void setSubtitles(List<Subtitle> subtitles) {
        this.subtitles = subtitles;
    }

    public void parseFromStream(InputStream stream, Map<String, Object> parameters) throws SubtitleFormatException {
        String inputEncoding = "UTF-8";
        if (parameters.containsKey(ParseParameters.INPUT_ENCODING)) {
            inputEncoding = (String) parameters.get(ParseParameters.INPUT_ENCODING);
        }
        
        List<String> lines = new ArrayList<String>();
        Scanner scanner = new Scanner(stream, inputEncoding);
        while (scanner.hasNextLine()) {
            lines.add(scanner.nextLine());
        }
        
        parse(lines);
    }

    public void writeToStream(OutputStream stream, Map<String, Object> parameters) throws SubtitleFormatException {
        String outputEncoding = "UTF-8";
        if (parameters.containsKey(ParseParameters.OUTPUT_ENCODING)) {
            outputEncoding = (String) parameters.get(ParseParameters.OUTPUT_ENCODING);
        }
        
        OutputStreamWriter writer = null;
        try {
            writer = new OutputStreamWriter(stream, outputEncoding);
            List<String> lines = formatForFile();
            for (String line : lines) {
                writer.write(line);
                writer.write(System.getProperty("line.separator"));
            }
        } catch (UnsupportedEncodingException uee) {
            throw new SubtitleFormatException("Unsupported output encoding!", uee);
        } catch (IOException ioex) {
            throw new SubtitleFormatException("Error occured while writing to output stream!", ioex);
        }
    }    

    public void parse(List<String> lines) throws SubtitleFormatException {
        try {
            ArrayList<String> rawSubtitle = new ArrayList<String>();
            for (String line : lines) {
                if (line.isEmpty() && (rawSubtitle.size() > 0)) {
                    Subtitle sub = new Subtitle();
                    sub.parse(rawSubtitle);
                    subtitles.add(sub);
                    rawSubtitle.clear();
                } else {
                    rawSubtitle.add(line);
                }
            }
            if (rawSubtitle.size() > 0) {
                Subtitle sub = new Subtitle();
                sub.parse(rawSubtitle);
                subtitles.add(sub);
            }
        } catch (Exception ex) {
            throw new SubtitleFormatException("Error occured during subtitles parse phase!", ex);
        }
    }

    public void timeShift(long miliseconds, long offset) {
        long adjustedShift = miliseconds;
        if (miliseconds < 0) {
            // if we are shifting to the left make sure that we don't start before
            // the begining of the movie (i.e. the first subtitles in negative time)
            for (Subtitle sub : subtitles) {
                if (sub.getTimeWindow().getStart().toMiliseconds() >= offset) {
                    adjustedShift =  -1 * Math.min(Math.abs(miliseconds), sub.getTimeWindow().getStart().toMiliseconds());
                    if (adjustedShift != miliseconds) {
                        Logger.getLogger("SubtitleUtils").log(Level.SEVERE, 
                                String.format("Time shift adjusted from %d to %d to avoid overlapping the start of the movie!",
                                miliseconds, adjustedShift));
                        break;
                    }
                }
            }
        }
        for (Subtitle sub : subtitles) {
            if (sub.getTimeWindow().getStart().toMiliseconds() >= offset) {
                sub.timeShift(adjustedShift);
            }
        }
    }

    public List<String> formatForFile() {
        List<String> lines = new ArrayList<String>();
        for (Subtitle sub : subtitles) {
            lines.addAll(sub.formatForFile());
            lines.add("");
        }
        return lines;
    }
}
