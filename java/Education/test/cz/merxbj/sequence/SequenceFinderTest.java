package cz.merxbj.sequence;

import org.junit.Test;
import static org.junit.Assert.*;

public class SequenceFinderTest {
    
    public SequenceFinderTest() {
    }
    
    @Test
    public void SimpleTest() {
        JardasSequenceFinder finder = new JardasSequenceFinder();
        int[] sequence = {1, -2, -3, 5, 4, -8, 9, -15};
        assertEquals(new Interval(3, 6), finder.findMaxSequence(sequence));
    }
    
    @Test
    public void LongerTest() {
        JardasSequenceFinder finder = new JardasSequenceFinder();
        int[] sequence = {1,2,3,-10,1,2,3,4,-12,3,9,-15,8};
        assertEquals(new Interval(9, 10), finder.findMaxSequence(sequence));
    }
    
    @Test
    public void TestWithNegativesThatContinueSequence() {
        JardasSequenceFinder finder = new JardasSequenceFinder();
        int[] sequence = {-1,-5,1,5,-2,-1,5,-8};
        assertEquals(new Interval(2, 6), finder.findMaxSequence(sequence));
    }
    
    @Test
    public void TestWithNegativesThatContinueSequenceTwiceSecond() {
        JardasSequenceFinder finder = new JardasSequenceFinder();
        int[] sequence = {-1,-5,1,5,-2,-1,5,-8,1,5,-2,-1,6};
        assertEquals(new Interval(8, 12), finder.findMaxSequence(sequence));
    }

    @Test
    public void TestWithNegativesThatContinueSequenceTwiceFirst() {
        JardasSequenceFinder finder = new JardasSequenceFinder();
        int[] sequence = {-1,-5,1,5,-2,-1,5,-8,1,5,-2,-1,4};
        assertEquals(new Interval(2, 6), finder.findMaxSequence(sequence));
    }
    
    @Test
    public void FirstFailureTest() {
        JardasSequenceFinder finder = new JardasSequenceFinder();
        int[] sequence = {-5,-10,2,4,6,-2,-4,-3,-3,2,8,0};
        assertEquals(new Interval(2, 4), finder.findMaxSequence(sequence));
    }
    
    @Test
    public void LeosFailureTest() {
        JardasSequenceFinder finder = new JardasSequenceFinder();
        int[] sequence = {-8,-5,-1,-4};
        assertEquals(new Interval(2, 2), finder.findMaxSequence(sequence));
    }
}
