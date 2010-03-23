package notwa.sql;

import java.util.*;

public class ParameterSet extends TreeSet<Parameter> {

    public ParameterSet() {
        super();
    }
    
    public ParameterSet(Parameter [] params) {
        super();
        for (Parameter p : params) {
            super.add(p);
        }
    }
}
