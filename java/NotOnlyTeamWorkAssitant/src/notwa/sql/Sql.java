/*
 * Sql
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

/**
 * Abstract class which only purpose is to hold the code definitions for the
 * Sql syntax.
 * <p>The properties of this class should be always used for abstracting out a sql
 * syntax.</p>
 * 
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public abstract class Sql {

    /**
     * SQL syntax abstraction regarding the typical Relations
     */
    public abstract class Relation {

        /**
         * Abstraction for the equalty relation
         */
        public static final String EQUALTY              = "=";
        
        /**
         * Abstraction for the greater relation
         */
        public static final String GREATER              = ">";

        /**
         * Abstraction for the less relation
         */
        public static final String LESS                 = "<";

        /**
         * Abstraction for the greater or equals relation
         */
        public static final String GREATER_OR_EQUALS    = ">=";

        /**
         * Abstraction for the less or equals relation
         */
        public static final String LESS_OR_EQUALS       = "<=";
    }

    /**
     * SQL syntax abstraction ragrading the other keywords
     */
    public abstract class Keyword {
        
        /**
         * Abstraction for the WHERE keyword
         */
        public static final String WHERE                = "WHERE";
    }

    /**
     * SQL syntax abstraction regarding the logical conjunctions
     */
    public abstract class Logical {

        /**
         * Abstraction for the conjunction
         */
        public static final String CONJUNCTION          = "AND";

        /**
         * Abstraction for the disjunction
         */
        public static final String DISJUNCTION          = "OR";
    }
}
