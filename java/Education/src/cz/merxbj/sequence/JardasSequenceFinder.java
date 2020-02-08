package cz.merxbj.sequence;

public class JardasSequenceFinder implements ISequenceFinder {
    
    /**
     * Find the beginning and the end of a continuous sequence within the given
     * sequence that has the maximum sum of its elements.
     * @param sequence
     * @return Interval in the following form: <begin,end>
     */
    @Override
    public Interval findMaxSequence(int[] sequence) {
        int begin = 0;
        int localSum = 0;
        int maxSum = Integer.MIN_VALUE;
        Interval result = new Interval(0, 0);
        
        for (int i = 0; i < sequence.length; i++) {
            localSum += sequence[i];
            
            if (localSum > maxSum) {
                // we have found a new maximum, let's remember it
                result = new Interval(begin, i);
                maxSum = localSum;
            } else if (localSum <= 0) {
                // a new begining after we went below zero
                localSum = 0;
                begin = i + 1;
            }
        }

        return result;
    }

    @Override
    public int findMaximum(int[] sequence) {
        int localSum = 0;
        int maxSum = 0;
        
        for (int i = 0; i < sequence.length; i++) {
            localSum += sequence[i];
            
            if (localSum > maxSum) {
                // we have found a new maximum, let's remember it
                maxSum = localSum;
            } else if (localSum <= 0) {
                // a new begining after we went below zero
                localSum = 0;
            }
        }

        return maxSum;
    }
}
