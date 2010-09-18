/*
 * CommandLine
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

package su.application;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class CommandLine {
    private String source;
    private String destination;
    private long milisecondsTimeShift;

    public static CommandLine parse(String[] args) {
        if (args.length < 3)
            System.out.println(usage());

        CommandLine cl = new CommandLine();
        cl.setSource(args[0]);
        cl.setDestination(args[1]);
        cl.setMilisecondsTimeShift(Integer.parseInt(args[2]));

        return cl;
    }

    private static String usage() {
        return "Usage: SubtitleUtils source destination shift(ms)";
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public long getMilisecondsTimeShift() {
        return milisecondsTimeShift;
    }

    public void setMilisecondsTimeShift(long milisecondsTimeShift) {
        this.milisecondsTimeShift = milisecondsTimeShift;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
