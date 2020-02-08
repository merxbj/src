package inheritance;

public class InheritanceTest {
    
    public static void main(String[] args) {
        Parent p = new Parent();
        System.out.println(p);
        Child c = (Child) p;
        System.out.println(c);
    }

    public static class Child extends Parent {
        int id;

        public Child(int id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return ((Integer) id).toString();
        }
    }

    public static class Parent {

        public Parent() {
        }

        @Override
        public String toString() {
            return "Parent";
        }

    }
}
