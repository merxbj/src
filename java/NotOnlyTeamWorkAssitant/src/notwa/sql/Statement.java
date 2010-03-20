package notwa.sql;

import notwa.common.LoggingInterface;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.sql.Timestamp;

public class Statement {
    private String type;
    private String innerRelation;
    private HashMap<String,String> mappings;
    private StringBuilder statement;
    private String nextKeyword;

    public Statement() {
        statement = new StringBuilder();
        mappings = new HashMap<String,String>();
    }

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

            LoggingInterface.getInstanece().handleException(ex);
            return false;
        }
        return true;
    }

    public String compileStatement() {
        return statement.toString();
    }

    public boolean hasParameter(String name) {
        return mappings.containsKey(name);
    }

    public void appendCondition(Parameter parameter) {
        statement.append(nextKeyword);
        statement.append(" ");
        statement.append(mappings.get(parameter.getName()));
        statement.append(" ");
        statement.append(parameter.getRelation());
        statement.append(" ");
        statement.append(formatValueForSql(parameter));
        nextKeyword = " " + innerRelation;
    }

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
