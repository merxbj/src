package notwa.gui;

public class JComboBoxItemCreator {
    Object object;
    String value;

    public JComboBoxItemCreator(Object object, String value) {
        this.object = object;
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    
    public Object getAttachedObject() {
        return object;
    }

    @Override
    public String toString() {
        return value;
    }
}
