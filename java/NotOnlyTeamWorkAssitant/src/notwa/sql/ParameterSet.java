/*
 * ParameterSet
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
package notwa.sql;

import java.util.TreeSet;

/**
 * The simple wrapper around the TreeSet enforcing a strong typed initialization
 * by even a single <code>Parameter</code> or by the <code>Parameter</code> array.
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class ParameterSet extends TreeSet<Parameter> {

    /**
     * The simplest constructor initializing an empty set.
     */
    public ParameterSet() {
        super();
    }

    /**
     * Constructor initializing the set with a single given <code>Parameter</code>.
     *
     * @param param The single <code>Parameter</code> to be added to the set.
     */
    public ParameterSet(Parameter param) {
        super();
        super.add(param);
    }
    
    /**
     * Constructor initializing the set with an array of given <code>Parameters</code>.
     *
     * @param params The array of <code>Parameters</code> to be added to the set.
     */
    public ParameterSet(Parameter [] params) {
        super();
        for (Parameter p : params) {
            super.add(p);
        }
    }
}
