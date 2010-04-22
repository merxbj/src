/*
 * Statement
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

import notwa.logger.LoggingFacade;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.sql.Timestamp;

/**
 * This <code>class</code> represents a single parameter placeholders statement
 * present in the SQL template.
 * This <code>Statement</code> should be than provided with the parameter collection
 * and it will produce the built SQL to be a part of the related SQL template.
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class Statement {
    private String type;
    private String innerRelation;
    private HashMap<String,String> mappings;
    private StringBuilder statement;
    private String nextKeyword;

    /**
     * The sole, parameter-less, constructor, initializing the class members.
     */
    public Statement() {
        statement = new StringBuilder();
        mappings = new HashMap<String, String>();
    }

    /**
     * Accepts the single raw statement factored out from the sql template and
     * brings him into the object look.
     * <p>It, at first, recognizes, how the given statement should be related with
     * its surroundings (It is the opening WHERE? Or continues it as AND? Or or?)
     * Than it creates parameter name to column name mappings, to be able to compile
     * the final result which should replace the parameter name with the actual
     * value found within the <code>Parameter</code>.</p>
     *
     * @param rawStatement The raw statement.
     * @return  <code>true</code> if the parsing process completes successfully,
     *          <code>false</code> otherwise.
     */
    public boolean parse(String rawStatement) {
        type = rawStatement.substring(rawStatement.indexOf("=") + 1, rawStatement.indexOf(";")).trim();
        innerRelation = rawStatement.substring(rawStatement.indexOf("=", rawStatement.indexOf("=") + 1) + 1, rawStatement.indexOf(";", rawStatement.indexOf(";") + 1)).trim();

        try {
            StringTokenizer relations = new StringTokenizer(rawStatement, "{}");
            relations.nextToken(); // skip the first token which is obviously the statement header parsed before
            while (relations.hasMoreTokens()) {
                String column = null;
                String parameter = null;
                StringTokenizer relation = new StringTokenizer(relations.nextToken(), ";=");
                while (relation.hasMoreTokens()) {
                    String token = relation.nextToken();
                    if (token.equals("column")) {
                        column = relation.nextToken().trim();
                    } else if (token.equals("parameter")) {
                        parameter = relation.nextToken().trim();
                    }
                    if (column != null && parameter != null) {
                        mappings.put(parameter, column);
                        column = null;
                        parameter = null;
                    }
                }
            }
            nextKeyword = type;
        } catch (Exception ex) {
            LoggingFacade.handleException(ex);
            return false;
        }
        return true;
    }

    /**
     * Returns the final SQL Query result built from the statement given at the
     * begining by mixing it with the actual <code>Parameter</code>s.
     *
     * @return The SQL Query part of the parent sql template.
     */
    public String compileStatement() {
        return statement.toString();
    }

    /**
     * Makes sure that this <code>Statement</code> does have the definition for
     * the <code>Parameter</code> identified by the given name.
     *
     * @param name The name of the parameter.
     * @return  <code>true</code> if this <code>Statement</code> knows the given
     *          parameter name, <code>false</code> otherwise.
     */
    public boolean hasParameter(String name) {
        return mappings.containsKey(name);
    }

    /**
     * Appends the given <code>Parameter</code> to this <code>Statement</code>
     * itteratively building the resulting SQL part.
     *
     * @param parameter The parameter where the value and the actual relation will
     *                  be found.
     */
    public void appendRelation(Parameter parameter) {
        statement.append(nextKeyword);
        statement.append(" ");
        statement.append(mappings.get(parameter.getName()));
        statement.append(" ");
        statement.append(parameter.getRelation());
        statement.append(" ");
        statement.append(formatValueForSql(parameter));
        nextKeyword = " " + innerRelation;
    }

    /**
     * Formats the value in the given parameter for the SQL Query. This actually
     * means that the <code>String</code> or the <code>Timestamp</code> will be
     * surrounded with quotas.
     * 
     * @param p The parameter which values will be well-formated.
     * @return The well-formated value.
     */
    private String formatValueForSql(Parameter p) {
        StringBuilder sb = new StringBuilder();
        Object o = p.getValue();
        if ((o instanceof String) || (o instanceof Timestamp)) {
            sb.append("'");
            if (o instanceof Timestamp) {
                sb.append(((Timestamp) o).toString());
            } else {
                sb.append((String) o);
            }
            sb.append("'");
        } else {
            sb.append(o);
        }
        return sb.toString();
    }
}
