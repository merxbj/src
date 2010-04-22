/*
 * Parameter
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
 * The <code>class</code> representing the single sql parameter which is build
 * from:
 * <ul>
 * <li>The parameter name</li>
 * <li>The parameter value</li>
 * <li>The relation between the name and the value</li>
 * <ul>
 * This parameter is then build into the SQL Query template where the actual
 * parameter placeholders are replaced with the values and columns as mandated
 * by the {@link ParameterSet} containg a set of instances of this <code>class</code>.
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class Parameter {
    private String name;
    private Object value;
    private String relation;
    
    /**
     * The sole constructor allowing to set all of the properties of valid 
     * <code>Parameter</code>.
     *
     * @param name The name of the parameter.
     * @param value The value of the parameter.
     * @param relation The relation between the value and the name.
     */
    public Parameter(String name, Object value, String relation) {
        this.name = name;
        this.value = value;
        this.relation = relation;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Parameter other = (Parameter) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
    
    /**
     * Gets this parameter name.
     * 
     * @return The parameter name.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets this parameter name.
     * 
     * @param name The parameter name.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Gets this parameter value.
     * 
     * @return The parameter value.
     */
    public Object getValue() {
        return value;
    }
    
    /**
     * Sets this parameter value.
     *
     * @param value The parameter value.
     */
    public void setValue(String value) {
        this.value = value;
    }
    
    /**
     * Gets this parameter relation between the name and the value.
     *
     * @return The name to value relation.
     */
    public String getRelation() {
        return this.relation;
    }
    
    /**
     * Sets this parameter relation between the name and the value.
     * 
     * @param relation The name to value relation.
     */
    public void setRelation(String relation) {
        this.relation = relation;
    }
}
