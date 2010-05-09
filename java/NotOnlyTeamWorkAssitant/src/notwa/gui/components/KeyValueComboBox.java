/*
 * KeyValueComboBox
 *
 * Copyright (C) 2010  Jaroslav Merxbauer
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package notwa.gui.components;

import javax.swing.JComboBox;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class KeyValueComboBox<TKey> extends JComboBox {

    public void addItem(ComboBoxItem<TKey> item) {
        super.addItem(item);
    }

    public void removeItem(ComboBoxItem<TKey> item) {
        super.removeItem(item);
    }

    public void setSelectedItem(ComboBoxItem<TKey> item) {
        super.setSelectedItem(item);
    }

    public void setSelectedKey(TKey key) {
        setSelectedItem(new ComboBoxItem<TKey>(key));
    }

    public TKey getSelectedKey() {
        return getSelectedItem() != null ? getSelectedItem().getKey() : null;
    }

    @Override
    public ComboBoxItem<TKey> getSelectedItem() {
        return (ComboBoxItem<TKey>) super.getSelectedItem();
    }
}
