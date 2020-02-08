/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.merxbj.logic.core;

/**
 *
 * @author jmerxbauer
 */
public class Evaluation {
    private int misplaced;
    private int wellplaced;

    public Evaluation(int misplaced, int wellplaced) {
        this.misplaced = misplaced;
        this.wellplaced = wellplaced;
    }
    
    public int getMisplaced() {
        return misplaced;
    }

    public int getWellplaced() {
        return wellplaced;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Evaluation other = (Evaluation) obj;
        if (this.misplaced != other.misplaced) {
            return false;
        }
        if (this.wellplaced != other.wellplaced) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 73 * hash + this.misplaced;
        hash = 73 * hash + this.wellplaced;
        return hash;
    }
}
