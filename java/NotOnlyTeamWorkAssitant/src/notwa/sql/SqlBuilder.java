/*
 * SqlBuilder
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

import java.util.ArrayList;
import notwa.dal.WorkItemDal;

/**
 * <code>SqlBuilder</code> provides the power to transform a SQL template into the
 * final SQL statement which can be exectuted againts the database.
 * <p>The main purpose is to provide a parametrized SQL where the parameters will
 * be replaced with the real values with the proper relations againts the coresponding
 * columns</p>
 * @see WorkItemDal#getSqlTemplate()
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class SqlBuilder {
    private StringBuilder sqltemplate;
    private ParameterSet parameters;
    private ArrayList<Statement> statements;
    
    /**
     * The sole constructor expecting the actual sql template and the parameters
     * that are going to be factored into the template.
     *
     * @param template The SQL template.
     * @param parameters The SQL parameters.
     */
    public SqlBuilder(String template, ParameterSet parameters) {
        this.sqltemplate = new StringBuilder(template);
        this.parameters = parameters;
        this.statements = new ArrayList<Statement>();
    }

    /**
     * Compiles the resulting SQL Query based on the given SQL Parameters through
     * the constructor.
     * 
     * @return The final SQL Query.
     */
    public String compileSql() {
        parseTemplate();

        for (Parameter p : parameters) {
            for (Statement s : statements) {
                if (s.hasParameter(p.getName())) {
                    s.appendRelation(p);
                }
            }
        }

        for (Statement s : statements) {
            String statementIdentifier = String.format("<s#%d>", statements.indexOf(s));
            int statementStart = sqltemplate.indexOf(statementIdentifier);
            sqltemplate.replace(statementStart, statementStart + statementIdentifier.length(), s.compileStatement());
        }

        return sqltemplate.toString();
    }
    
    /**
     * Parses the given SQL template which actually means that it pulls out the
     * STATEMENT sections and puts their object representation into the collection.
     */
    private void parseTemplate() {
        int statementStart = sqltemplate.indexOf("/** STATEMENT");
        int statementEnd = 0;
        while (statementStart != -1) {
            statementEnd = sqltemplate.indexOf("**/", statementStart) + 3;
            if (statementEnd != -1) {
                Statement s = new Statement();
                if (s.parse(sqltemplate.substring(statementStart, statementEnd))) {
                    statements.add(s);
                    sqltemplate.replace(statementStart, statementEnd, String.format("<s#%d>", statements.indexOf(s)));
                }
            }
            statementStart = sqltemplate.indexOf("/** STATEMENT", statementEnd);
        }
    }
}
