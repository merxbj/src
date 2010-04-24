/*
 * ContextManager
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
package notwa.wom;

import java.util.Hashtable;

/**
 * The ContextManager is a singleton based class which main and only purpose is
 * to maintain <code>Context</code>s.
 * <p> It provides method to create and return a new context, which is then house 
 * keeped and could be acquired afterwards again by their contextId.<p>
 * 
 * @author jmerxbauer
 */
public class ContextManager {
    private static ContextManager instance;
    private Hashtable<Integer, Context> contexts;
    private int nextContextId;

    /**
     * Get the actual singleton instance of this <code>ContextManager</code>.
     *
     * @return The singleton instance of this <code>ContextManager</code>
     */
    public static ContextManager getInstance() {
        if (instance == null) {
            instance = new ContextManager();
        }
        return instance;
    }

    /**
     * The sole, hidden contructor which should not be used from outside this class.
     */
    protected ContextManager() {
        this.contexts = new Hashtable<Integer, Context>();
        this.nextContextId = 0;
    }

    /**
     * Gets the <code>Context</code> requested by the given contextId which has 
     * been previously created and housekeeped by this <code>ContextManager</code>.
     *
     * @param contextId The uniqe identifier of requeste <code>Context</code>.
     * @return 
     */
    public Context getContext(int contextId) {
        return contexts.get(contextId);
    }

    /**
     * Creates a new <code>Context</code> with the new contextId. This <code>Context</code>
     * is then housekeeped and could be acquired again byt the {@link #getContext(int)}.
     *
     * @return The newly created <code>Context</code>.
     */
    public Context newContext() {
        Context context = new Context(nextContextId);
        this.contexts.put(nextContextId, context);
        nextContextId++;
        return context;
    }
}
