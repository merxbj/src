package inheritance;

public class InheritanceTest {
    
    public static void main(String[] args) {
        Comparable comp1 = new Child(1);
        Comparable comp2 = new Child(2);
        System.out.println(comp1.compareTo(comp2));
    }

    public static class Child extends Parent<Child> {

        int id;

        public Child(int id) {
            this.id = id;
        }
        
        @Override
        public int compareTo(Child o) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    public static abstract class Parent<T extends Parent> implements Comparable<T> {

        public abstract int compareTo(T o);

    }
}
