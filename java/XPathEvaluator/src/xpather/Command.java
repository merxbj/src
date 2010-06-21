package xpather;

import java.util.ArrayList;
import java.util.Collection;

public class Command {
    private String query;
    private Collection<String> parameters;

    public Command() {
        this.query = "";
        this.parameters = new ArrayList<String>();
    }

    public String getCommand() {
        return query;
    }

    public void setQuery(String command) {
        this.query = command;
    }

    public void clear() {
        this.query = "";
        this.parameters.clear();
    }

    public Collection<String> getParameters() {
        return parameters;
    }

    public void addParameter(String parameter) {
        this.parameters.add(parameter);
    }

    public boolean hasParameter(String parameter) {
        return this.parameters.contains(parameter);
    }
}
