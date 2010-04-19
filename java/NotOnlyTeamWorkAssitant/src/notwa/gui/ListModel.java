package notwa.gui;

import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.DefaultListModel;

public class ListModel extends DefaultListModel {

    @Override
    public Object[] toArray() {
        ArrayList<Object> array = new ArrayList<Object>();
        for (int i = 0; i<super.size(); i++) {
            array.add(super.getElementAt(i).toString());
        }
        return array.toArray();
    }
}