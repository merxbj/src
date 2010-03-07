package notwa.gui;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

@SuppressWarnings("serial")
public class WITTable extends JPanel{
	ArrayList<Object[]> data = new ArrayList<Object[]>();
	
	public WITTable() {
		this.setLayout(new BorderLayout());
		
		fillWits(); //temp
		
		TblModel witTableModel = new TblModel(data);
		JTable witTable = new JTable(witTableModel);
	    
		witTable.getColumnModel().getColumn(0).setHeaderValue("Product");
		witTable.getColumnModel().getColumn(1).setHeaderValue("WIT ID");
		witTable.getColumnModel().getColumn(2).setHeaderValue("Subject");
		witTable.getColumnModel().getColumn(3).setHeaderValue("Priority");
		witTable.getColumnModel().getColumn(4).setHeaderValue("Assigned");
		witTable.getColumnModel().getColumn(5).setHeaderValue("Status");
		
		JScrollPane jsp = new JScrollPane(witTable);
		
		this.add(jsp, BorderLayout.CENTER);
	}
	
	private void fillWits() {
		data.add(new Object[]{"notwa", "XX-00001", "Do this class", "Critical", "mrneo","working on"});
		data.add(new Object[]{"notwa", "XX-00002", "Do another class", "Critical", "mrneo","working on"});
		data.add(new Object[]{"notwa", "XX-00003", "Do b class", "Critical", "mrneo","working on"});
		data.add(new Object[]{"notwa", "XX-00004", "Do c class", "Critical", "mrneo","working on"});
	}
}
