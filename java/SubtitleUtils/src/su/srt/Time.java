/*
 * Time
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
class Time implements FileRepresentable {
    private int hours = 0;
    private int minutes = 0;
    private int seconds = 0;
    private int miliseconds = 0;

    public Time() {
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        if (hours < 0)
            throw new RuntimeException("Hours cannot be less than zero!");
        this.hours = hours;
    }

    public int getMiliseconds() {
        return miliseconds;
    }

    public void setMiliseconds(int miliseconds) {
        if (miliseconds < 0 || miliseconds > 999)
            throw new RuntimeException("Miliseconds cannot be less than zero or greater than 999!");
        this.miliseconds = miliseconds;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        if (minutes < 0 || minutes > 59)
            throw new RuntimeException("Minutes cannot be less than zero or greater than 59!");
        this.minutes = minutes;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        if (seconds < 0 || seconds > 59)
            throw new RuntimeException("Seconds cannot be less than zero or greater than 59!");
        this.seconds = seconds;
    }

    public void parse(List<String> lines) throws SubtitleFormatException {
        String rawTime = lines.get(0);
        String[] tokens = rawTime.split("[:,\\,]");
        if (tokens.length != 4)
            throw new SubtitleFormatException(String.format("Invalid time format (%s)", rawTime));

        try {
            setHours(Integer.parseInt(tokens[0]));
            setMinutes(Integer.parseInt(tokens[1]));
            setSeconds(Integer.parseInt(tokens[2]));
            setMiliseconds(Integer.parseInt(tokens[3]));
        } catch (Exception ex) {
            clear();
            throw new SubtitleFormatException(String.format("Invalid time format (%s)", rawTime), ex);
        }
    }

    public void clear() {
        hours = 0;
        minutes = 0;
        seconds = 0;
        miliseconds = 0;
    }

    public long toMiliseconds() {
        return (miliseconds + (seconds * 1000) + (minutes * 60000) + (hours * 3600000));
    }

    public void fromMiliseconds(long miliseconds) {
        hours = (int)(miliseconds / 3600000);
        minutes = (int)((miliseconds % 3600000) / 60000);
        seconds = (int)(((miliseconds % 3600000) % 60000) / 1000);
        miliseconds = (int)(((miliseconds % 3600000) % 60000) % 1000);
    }

    public void substract(long miliseconds) {
        fromMiliseconds(toMiliseconds() - miliseconds);
    }

    public void add(long miliseconds) {
        fromMiliseconds(toMiliseconds() + miliseconds);
    }

    public List<String> formatForFile() {
        List<String> lines = new ArrayList<String>();
        lines.add(String.format("%02d:%02d:%02d,%03d", hours, minutes, seconds, miliseconds));
        return lines;
    }
}
