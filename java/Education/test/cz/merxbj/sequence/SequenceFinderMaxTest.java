package cz.merxbj.sequence;

import org.junit.Test;
import static org.junit.Assert.*;

public class SequenceFinderMaxTest {
    
    public SequenceFinderMaxTest() {
    }
    
    @Test
    public void SimpleTest() {
        HonzasSequenceFinder finder = new HonzasSequenceFinder();
        int[] sequence = {1, -2, -3, 5, 4, -8, 9, -15};
        assertEquals(10, finder.findMaximum(sequence));
    }
    
    @Test
    public void LongerTest() {
        HonzasSequenceFinder finder = new HonzasSequenceFinder();
        int[] sequence = {1,2,3,-10,1,2,3,4,-12,3,9,-15,8};
        assertEquals(12, finder.findMaximum(sequence));
    }
    
    @Test
    public void TestWithNegativesThatContinueSequence() {
        HonzasSequenceFinder finder = new HonzasSequenceFinder();
        int[] sequence = {-1,-5,1,5,-2,-1,5,-8};
        assertEquals(8, finder.findMaximum(sequence));
    }
    
    @Test
    public void TestWithNegativesThatContinueSequenceTwiceSecond() {
        HonzasSequenceFinder finder = new HonzasSequenceFinder();
        int[] sequence = {-1,-5,1,5,-2,-1,5,-8,1,5,-2,-1,6};
        assertEquals(9, finder.findMaximum(sequence));
    }

    @Test
    public void TestWithNegativesThatContinueSequenceTwiceFirst() {
        HonzasSequenceFinder finder = new HonzasSequenceFinder();
        int[] sequence = {-1,-5,1,5,-2,-1,5,-8,1,5,-2,-1,4};
        assertEquals(8, finder.findMaximum(sequence));
    }
    
    @Test
    public void FirstFailureTest() {
        HonzasSequenceFinder finder = new HonzasSequenceFinder();
        int[] sequence = {-5,-10,2,4,6,-2,-4,-3,-3,2,8,0};
        assertEquals(12, finder.findMaximum(sequence));
    }
}
