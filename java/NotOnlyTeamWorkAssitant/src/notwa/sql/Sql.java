package notwa.sql;

public abstract class Sql {

    public abstract class Relation {
        public static final String EQUALTY              = "=";
        public static final String GREATER              = ">";
        public static final String LESS                 = "<";
        public static final String GREATER_OR_EQUALS    = ">=";
        public static final String LESS_OR_EQUALS       = "<=";
    }

    public abstract class Keyword {
        public static final String WHERE                = "WHERE";
    }

    public abstract class Logical {
        public static final String CONJUNCTION          = "AND";
        public static final String DISJUNCTION          = "OR";
    }
}
