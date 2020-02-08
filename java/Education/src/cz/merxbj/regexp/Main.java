/*
 * Main
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

package cz.merxbj.regexp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Jaroslav Merxbauer
 */
public class Main {
    public static void main(String[] args) {
        Pattern pat = Pattern.compile("Temp([0-9]+)([a-zA-Z]+)([0-9]{8})-([0-9]{20})-([0-9]{10})(?:-([0-9]{3})|)$");
        Matcher match = pat.matcher("Temp56HEADER20111113-00000000990000000016-0000000001");
        if (match.find()) {
            System.out.println(match.group());
        } else {
            System.out.println("Nothing");
        }
    }
}
