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
public class HonzasSequenceFinder implements ISequenceFinder {

    @Override
    public Interval findMaxSequence(int[] sequence) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int findMaximum(int[] sequence) {
        int max = 0;
        int sum = 0;

        for (int i = 0; i < sequence.length; i++) {

            sum = sequence[i] + sum;

            if (sum > max) {
                max = sum;
            }

            if (sum <= 0) {
                sum = 0;
            }
        }
        return max;
    }
    
    
}
