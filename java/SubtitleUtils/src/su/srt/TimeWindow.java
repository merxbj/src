/*
 * TimeWindow
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import su.common.FileRepresentable;
import su.common.SubtitleFormatException;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
class TimeWindow implements FileRepresentable {
    private Time start;
    private Time end;

    public TimeWindow() {
        start = new Time();
        end = new Time();
    }

    public Time getEnd() {
        return end;
    }

    public void setEnd(Time end) {
        this.end = end;
    }

    public Time getStart() {
        return start;
    }

    public void setStart(Time start) {
        this.start = start;
    }

    public void parse(List<String> lines) throws SubtitleFormatException {
        String rawTimeWindow = lines.get(0);
        List<String> tokens = new ArrayList<String>(Arrays.asList(rawTimeWindow.split(" --> ")));
        if (tokens.size() != 2)
            throw new SubtitleFormatException(String.format("Invalid time window format (%s)", rawTimeWindow));

        try {
            start.parse(tokens.subList(0, 1));
            end.parse(tokens.subList(1, 2));
        } catch (Exception ex) {
            throw new SubtitleFormatException(String.format("Invalid time window format (%s)", rawTimeWindow), ex);
        }
    }

    public void shift(long miliseconds) {
        start.add(miliseconds);
        end.add(miliseconds);
    }

    public List<String> formatForFile() {
        StringBuilder builder = new StringBuilder();
        List<String> lines = new ArrayList<String>();
        builder.append(start.formatForFile().get(0)).append(" --> ").append(end.formatForFile().get(0));
        lines.add(builder.toString());
        return lines;
    }
}
