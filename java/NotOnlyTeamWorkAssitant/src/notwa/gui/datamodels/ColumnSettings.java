/*
 * ColumnSettings
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

package notwa.gui.datamodels;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class ColumnSettings<T extends Enum<?>> implements Comparable<ColumnSettings<?>> {
    private int columnIndex;
    private Class<?> columnClass;
    private String columnHeader;
    private T columnAlias;

    public ColumnSettings(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    public ColumnSettings(int columnIndex, Class<?> columnClass, String columnHeader, T columnAlias) {
        this.columnIndex = columnIndex;
        this.columnClass = columnClass;
        this.columnHeader = columnHeader;
        this.columnAlias = columnAlias;
    }

    @Override
    public int compareTo(ColumnSettings<?> o) {
        return ((Integer)o.columnIndex).compareTo(columnIndex);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ColumnSettings<?> other = (ColumnSettings<?>) obj;
        if (this.columnIndex != other.columnIndex) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 19 * hash + this.columnIndex;
        return hash;
    }

    public T getColumnAlias() {
        return columnAlias;
    }

    public void setColumnAlias(T columnAlias) {
        this.columnAlias = columnAlias;
    }

    public Class<?> getColumnClass() {
        return columnClass;
    }

    public void setColumnClass(Class<?> columnClass) {
        this.columnClass = columnClass;
    }

    public String getColumnHeader() {
        return columnHeader;
    }

    public void setColumnHeader(String columnHeader) {
        this.columnHeader = columnHeader;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public void setColumnIndex(int columnIndex) {
        this.columnIndex = columnIndex;
    }
}
