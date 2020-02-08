/*
 * Subtitle
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
import java.util.List;
import su.common.SubtitleFormatException;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
class Subtitle implements FileRepresentable {
    private int id;
    private TimeWindow timeWindow;
    private List<String> subtitles;

    public Subtitle() {
        id = 0;
        timeWindow = new TimeWindow();
        subtitles = new ArrayList<String>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<String> getSubtitles() {
        return subtitles;
    }

    public void setSubtitles(ArrayList<String> subtitles) {
        this.subtitles = subtitles;
    }

    public TimeWindow getTimeWindow() {
        return timeWindow;
    }

    public void setTw(TimeWindow tw) {
        this.timeWindow = tw;
    }

    public void parse(List<String> rawSubtitle) throws SubtitleFormatException {
        if (rawSubtitle.size() < 3) {
            throw new SubtitleFormatException(String.format("Invalid subtitle format (%s)", formatRawSubForException(rawSubtitle)));
        }

        try {
            id = Integer.parseInt(rawSubtitle.get(0));
            timeWindow.parse(rawSubtitle.subList(1,2));
            subtitles.addAll(rawSubtitle.subList(2, rawSubtitle.size()));
        } catch (Exception ex) {
            throw new SubtitleFormatException(String.format("Invalid subtitle format (%s)", formatRawSubForException(rawSubtitle)), ex);
        }

    }

    private String formatRawSubForException(List<String> rawSubtitle) {
        StringBuilder sb = new StringBuilder("\n");
        for (String line : rawSubtitle) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }

    public void timeShift(long miliseconds) {
        timeWindow.shift(miliseconds);
    }

    public List<String> formatForFile() {
        List<String> lines = new ArrayList<String>();
        lines.add(String.format("%d", id));
        lines.addAll(timeWindow.formatForFile());
        lines.addAll(subtitles);
        return lines;
    }
}
