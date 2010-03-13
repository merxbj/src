package notwa.sql;

import java.util.ArrayList;

public class SqlBuilder {
    private StringBuilder sqlPattern;
    private ParameterCollection parameters;
    private ArrayList<Statement> statements;
    
    
    public SqlBuilder(String sqlPattern, ParameterCollection parameters) {
        this.sqlPattern = new StringBuilder(sqlPattern);
        this.parameters = parameters;
        this.statements = new ArrayList<Statement>();
    }
    
    private void addEqualtyCondition(Parameter parameter) {
        
    }
    
    private void addGreaterCondition(Parameter parameter) {
        
    }
    
    private void addGreaterOrEqualsCondition(Parameter parameter) {
        
    }

    private void addLessCondition(Parameter parameter) {
    
    }

    private void addLessOrEqualsCondition(Parameter parameter) {
    
    }
    
    private void parseQuery() {
        int statementStart = sqlPattern.indexOf("/** STATEMENT");
        int statementEnd = 0;
        while (statementStart != -1) {
            statementEnd = sqlPattern.indexOf("**/", statementStart);
            if (statementEnd != -1) {
                Statement s = new Statement();
                if (s.parse(sqlPattern.substring(statementStart, statementEnd))) {
                    statements.add(s);
                    sqlPattern.replace(statementStart, statementEnd, String.format("s#%d", statements.indexOf(s)));
                }
            }
        }
    }
    
    public String compileSql() {
        parseQuery();
        
        for (Parameter p : parameters) {
            
        }

        return "";
    }
}
