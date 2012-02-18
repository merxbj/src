/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.merxbj.logic.core;

import java.util.HashMap;
import java.util.Objects;

/**
 *
 * @author jmerxbauer
 */
public class Solution implements Evaluable {
    HashMap<Integer, Marble> solution;

    public Solution() {
        this.solution = new HashMap<>();
    }
    
    void set(int position, Marble marble) {
        solution.put(position, marble);
    }

    @Override
    public String toString() {
        return "Solution{" + "solution=" + solution + '}';
    }

    @Override
    public Evaluation evaluate(Solution other) {
        if (!this.solution.keySet().equals(other.solution.keySet())) {
            throw new RuntimeException(String.format("Cannot evaluate this solution (%s) with %s.", this.solution, other.solution));
        }
        
        int missplaced = 0;
        int wellplaced = 0;
        for (Integer pos : this.solution.keySet()) {
            if (other.solution.get(pos).equals(this.solution.get(pos))) {
                wellplaced++;
            } else if (other.solution.containsValue(this.solution.get(pos))) {
                missplaced++;
            }
        }
        
        return new Evaluation(missplaced, wellplaced);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Solution other = (Solution) obj;
        if (!Objects.equals(this.solution, other.solution)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.solution);
        return hash;
    }
}
