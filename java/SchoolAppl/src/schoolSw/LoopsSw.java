package schoolSw;

import java.util.*;

public class LoopsSw extends common.BaseExectuableClass {

   public LoopsSw(boolean wantRun) {
       super(wantRun);
   }

    public void Execute() {

        System.out.println("Please enter sequence of numbers ended with zero: ");

        int max = 0;
        int secondMax = 0;
        long sum = 0;
        int count = 0;
        int previous = 0;
        Sequence seq = Sequence.unknown;
        int diff = 0;
        double quoc = 0;
        boolean arithmetic = true;
        boolean geometric = true;

        int element = sc.nextInt();
        while (element != 0) {
            
            // examine avarage
            count++;
            sum += element;

            // examine max values
            if (max == 0) {
                max = element;
                secondMax = element;
            }
            else if (element > max) {
                secondMax = max;
                max = element;
            }
            else if (element > secondMax) {
                secondMax = element;
            }

            // store actual element as previous
            previous = element;

            // get next element
            element = sc.nextInt();

            // now examine consecution
            if (element != 0) {
                switch (seq) {
                    case unknown:
                        if (previous < element) {
                            seq = Sequence.increasing;
                        }
                        else if (previous > element) {
                            seq = Sequence.decreasing;
                        }
                        else {
                            seq = Sequence.constant;
                        }
                        diff = element - previous;
                        quoc = (double)element / previous;
                        break;
                    case increasing:
                    case nonIncreasing:
                        if (previous == element) {
                            seq = Sequence.nonIncreasing;
                        }
                        else if (previous > element){
                            seq = Sequence.notSequence;
                        }
                        if ((element - previous) != diff) {
                            arithmetic = false;
                        }
                        if (((double)element / previous) != quoc) {
                            geometric = false;
                        }
                        break;
                    case decreasing:
                    case nonDecreasing:
                        if (previous == element) {
                            seq = Sequence.nonDecreasing;
                        }
                        else if (previous < element) {
                            seq = Sequence.notSequence;
                        }
                        if ((element - previous) != diff) {
                            arithmetic = false;
                        }
                        if (((double)element / previous) != quoc) {
                            geometric = false;
                        }
                        break;
                    case constant:
                        if (previous < element) {
                            seq = Sequence.nonIncreasing;
                        }
                        else if (previous > element) {
                            seq = Sequence.nonDecreasing;
                        }
                        break;
                } // switch
            } // if
        } // while

        System.out.println("Max is " + max);
        System.out.println("Second max is " + secondMax);
        System.out.println("Avarage is " + ((count > 0) ? ((double)sum / count) : ("Error")));
        System.out.println("Consecution is " + seq.toString());
        if (arithmetic) {
            System.out.println("Consecution is arithmetic and it's difference is " + diff);
        }
        else if (geometric) {
            System.out.println("Consecution is geometric and it's quocient is " + quoc);
        }
    }

    private static Scanner sc = new Scanner(System.in);

    private enum Sequence {
        unknown,
        constant,
        increasing,
        decreasing,
        nonIncreasing,
        nonDecreasing,
        notSequence,
    }
}
