package notwa.gui.datamodels;

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.DefaultListModel;
import notwa.gui.components.KeyValueItem;

public class KeyValueListModel<TKey> extends DefaultListModel {

    public Collection<TKey> toCollection() {
        ArrayList<TKey> col = new ArrayList<TKey>();
        for (int i = 0; i < super.size(); i++) {
            col.add(getKeyAt(i));
        }
        return col;
    }

    public void addElement(TKey key, String value) {
        super.addElement(new KeyValueItem<TKey>(key, value));
    }

    public TKey getKeyAt(int index) {
        KeyValueItem<TKey> kvi = (KeyValueItem<TKey>) super.getElementAt(index);
        return (kvi != null) ? kvi.getKey() : null;
    }

    public boolean removeKey(TKey key) {
        return super.removeElement(new KeyValueItem<TKey>(key));
    }
}