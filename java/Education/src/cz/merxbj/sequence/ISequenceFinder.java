/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.merxbj.sequence;

/**
 *
 * @author jm185267
 */
public interface ISequenceFinder {

    /**
     * Find the beginning and the end of a continuous sequence within the given
     * sequence that has the maximum sum of its elements.
     * @param sequence
     * @return Interval in the following form: <begin,end>
     */
    Interval findMaxSequence(int[] sequence);
    
    int findMaximum(int[] sequence);
    
}
