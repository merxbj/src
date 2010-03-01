package FTPSynchronizer;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

@SuppressWarnings("serial")
class TblModel extends AbstractTableModel
{
	private final int COLUMN_COUNT = 2;
	private ArrayList<Object[]> rowData;

	public TblModel(ArrayList<Object[]> rowData)
	{
		this.rowData = rowData;
	}

	public int getColumnCount()
	{
		return COLUMN_COUNT;
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
		if(columnIndex == 0) // we dont want to edit first column
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