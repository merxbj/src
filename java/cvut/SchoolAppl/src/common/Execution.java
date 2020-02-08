package common;

import schoolSw.*;
import java.util.*;

public class Execution {

    public static void main(String[] args) {

        // Create instances of all our Runable classes
        Executable obj = new LoopsSw(true);
        objectCollection.add(obj);

        // Iterates through collection and run each object
        for (Executable e : objectCollection) {
            if (e != null && e.ReadyForExecution()) {
                e.Execute();
            }
        }
    }

    private static LinkedList<Executable> objectCollection = new LinkedList<Executable>();
}
