package reflections;

import java.lang.reflect.*;

public class BaseClass {
    private int bcMember;

    public BaseClass(int bcMember) {
        this.bcMember = bcMember;
    }

    public void printFields() {
        Class<?> c = this.getClass();
        for (Field f : c.getDeclaredFields()) {
            System.out.println(f.getName());
        }
    }
}
