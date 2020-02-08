package reflections;

public class ReflectionTest {
    public static void main(String[] args) {
        ConcreteClass cc = new ConcreteClass(2);
        cc.printFields();
        System.out.println("----");
        BaseClass bc = cc;
        bc.printFields();
        System.out.println("----");
        BaseClass bc2 = new BaseClass(1);
        bc2.printFields();
    }
}
