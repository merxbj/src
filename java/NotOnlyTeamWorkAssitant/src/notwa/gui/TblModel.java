package notwa.gui;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

@SuppressWarnings("serial")
class TblModel extends AbstractTableModel
{
	private ArrayList<Object[]> rowData;
	private String[] tableHeaders;

	public TblModel(ArrayList<Object[]> rowData, String[] tableHeaders)
	{
		this.rowData = rowData;
		this.tableHeaders = tableHeaders;
	}
	
	public String getColumnName(int c) {
		return tableHeaders[c];
	}
	
	public int getColumnCount()
	{
		return tableHeaders.length;
	}

	public int getRowCount()
	{
		return rowData == null ? 0 : rowData.size();
	}

	public void setValueAt(Object value, int rowIndex, int columnIndex)
	{
		getRecord(rowIndex)[columnIndex] = value;
		super.fireTableCellUpdated(rowIndex, columnIndex);
	}

	public Object getValueAt(int rowIndex, int columnIndex)
	{
		return getRecord(rowIndex)[columnIndex];
	}

	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		if(columnIndex < 3) // this will be more accurate //TODO: can be edited by owner of wit and so on ...
			return false;
		return true;
	}

	@SuppressWarnings("unchecked")
	public Class getColumnClass(int columnIndex)
	{
		if (rowData == null || rowData.size() == 0)
		{
			return Object.class;
		}
		Object o = getValueAt(0, columnIndex);
		return o == null ? Object.class : o.getClass();
	}

	private Object[] getRecord(int rowIndex)
	{
		return (Object[]) rowData.get(rowIndex);
	}
}