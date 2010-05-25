/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package notwa.gui.components;

import java.util.ArrayList;
import java.util.Collection;
import javax.swing.JList;
import notwa.gui.datamodels.KeyValueListModel;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class KeyValueList<TKey> extends JList {

    public KeyValueList(KeyValueListModel dataModel) {
        super(dataModel);
    }

    public TKey getSelectedKey() {
        KeyValueItem<TKey> kvi = (KeyValueItem<TKey>) super.getSelectedValue();
        return (kvi != null) ? kvi.getKey() : null;
    }

    @Override
    public String getSelectedValue() {
        KeyValueItem<TKey> kvi = (KeyValueItem<TKey>) super.getSelectedValue();
        return (kvi != null) ? kvi.toString() : null;
    }

    @Override
    public String[] getSelectedValues() {
        Object[] os = super.getSelectedValues();
        String[] values = null;
        if (os != null) {
            values = new String[os.length];
            for (int i =0; i < os.length; i++) {
                values[i] = os[i].toString();
            }
        }
        return values;
    }

    public Collection<TKey> getSelectedKeys() {
        Object[] os = super.getSelectedValues();
        ArrayList<TKey> keys = null;
        if (os != null) {
            keys = new ArrayList<TKey>(os.length);
            for (Object o : os) {
                keys.add(((KeyValueItem<TKey>) o).getKey());
            }
        }
        return keys;
    }
}
