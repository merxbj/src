package notwa.wom;

import java.util.Hashtable;

public class ContextManager {
    private Hashtable<Integer, Context> contexts;
    private int nextContextId;
    protected static ContextManager instance;

    public static ContextManager getInstance() {
        if (instance == null) {
            instance = new ContextManager();
        }
        return instance;
    }

    protected ContextManager() {
        this.contexts = new Hashtable<Integer, Context>();
        this.nextContextId = 0;
    }

    public Context getContext(int contextId) {
        return contexts.get(contextId);
    }

    public Context newContext() {
        Context context = new Context(nextContextId);
        this.contexts.put(nextContextId, context);
        nextContextId++;
        return context;
    }
}
