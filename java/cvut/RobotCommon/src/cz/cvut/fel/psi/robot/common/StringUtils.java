/*
 * StringUtils
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

package cz.cvut.fel.psi.robot.common;

import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class StringUtils {

    /**
     * 
     * C&P from http://snippets.dzone.com/posts/show/91 (ygmarchi on Apr 16, 2010 at 10:42)
     * @param s
     * @param delimiter
     * @return
     */
    public static String join(List<? extends CharSequence> s, String delimiter) {
	int capacity = 0;
	int delimLength = delimiter.length();
	Iterator<? extends CharSequence> iter = s.iterator();
	if (iter.hasNext()) {
	    capacity += iter.next().length() + delimLength;
	}

	StringBuilder buffer = new StringBuilder(capacity);
	iter = s.iterator();
	if (iter.hasNext()) {
	    buffer.append(iter.next());
	    while (iter.hasNext()) {
		buffer.append(delimiter);
		buffer.append(iter.next());
	    }
	}
	return buffer.toString();
    }

}
