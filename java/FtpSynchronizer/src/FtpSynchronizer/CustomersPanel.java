package FTPSynchronizer;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

import FTPSynchronizer.FTPSyncMainWindow.mysqlCollumns;

@SuppressWarnings("serial")
public class CustomersPanel extends JPanel implements ActionListener
{
	JFrame areYouSureFrame;
	static JScrollPane scrollPanelCustomersTable;
	JCheckBox customersCheckBox;
	static JButton chooseAllCustomers,uncheckAllCustomers,addNewCustomer,removeCustomer,editCustomer;
	JButton buttonOk, buttonStorno;
	static JTable customersTable;
	private TblModel customersTableModel;
	JPanel buttonsPanel;
	
	CheckBoxHeader chbh;
	
	boolean isChecked;
	
	public CustomersPanel()
	{
	    initButtons();
	    
		queryCustomersList();

		customersTableModel = new TblModel(FTPSyncMainWindow.customers);
	    customersTable = new JTable(customersTableModel);
	    customersTable.getColumnModel().getColumn(1).setMaxWidth(20);
	    customersTable.getColumnModel().getColumn(0).setHeaderValue("Zakaznici");
	    
	    chbh = new CheckBoxHeader();
	    chbh.addActionListener(this);
	    customersTable.getColumnModel().getColumn(1).setHeaderRenderer(chbh);
	    isChecked = false;
	    
	    JTableCellRenderer tableCellRenderer = new JTableCellRenderer();
	    customersTable.getColumnModel().getColumn(0).setCellRenderer(tableCellRenderer);
	    
	    scrollPanelCustomersTable = new JScrollPane(customersTable);
	    TableColumn fileCheckCustomerColumn = customersTable.getColumnModel().getColumn(1);
	    customersCheckBox = new JCheckBox();
	    fileCheckCustomerColumn.setCellEditor(new DefaultCellEditor(customersCheckBox));
	    
	    
	    setLayout(new BorderLayout());
	    add(scrollPanelCustomersTable, BorderLayout.CENTER);
	    add(buttonsPanel, BorderLayout.PAGE_END);
	}
	
	private void initButtons()
	{
		buttonsPanel = new JPanel();

	    addNewCustomer = new JButton("Add new");
	    editCustomer = new JButton("Edit");
	    removeCustomer = new JButton("Remove");
	    
	    addNewCustomer.addActionListener(this);
	    removeCustomer.addActionListener(this);
	    editCustomer.addActionListener(this);
	    
	    buttonsPanel.add(addNewCustomer);
	    buttonsPanel.add(editCustomer);
	    buttonsPanel.add(removeCustomer);
	    
	    buttonsPanel.setLayout(new GridLayout(1,0));
	}
	
	public static void queryCustomersList()
    {
		FTPSyncMainWindow.customers.clear();
		
		try{
    		 	Connection connection = mysqlConnection();
    		 	
	    		Statement statement = connection.createStatement();
	    		 
	    		statement.executeUpdate(	"CREATE TABLE IF NOT EXISTS `customers` ( "+
	    				 					"`name` varchar(255) NOT NULL,"+
	    				 					"`url` varchar(500) NOT NULL,"+
	    				 					"`subdir` varchar(50) NOT NULL,"+
	    				 					"`connName` varchar(200) NOT NULL,"+
	    				 					"`connPass` varchar(200) NOT NULL,"+
	    				 					"PRIMARY KEY  (`name`)"+
	    									") ENGINE=MyISAM DEFAULT CHARSET=utf8;");
	    		 
	    		ResultSet mysqlQuery = statement.executeQuery("SELECT * FROM customers");
	    		while (mysqlQuery.next())
	    		{
	    			 // Last field is ENUM ('E'=Error, 'D'=Done, 'X'=Starting(not yet started)
	    		FTPSyncMainWindow.customers.add(new Object[]{
	    		 						mysqlQuery.getString(mysqlCollumns.CUSTOMERNAME.ordinal()),
	    				 				false,
	    				 				mysqlQuery.getString(mysqlCollumns.URL.ordinal()),
	    				 				mysqlQuery.getString(mysqlCollumns.SUBDIR.ordinal()),
	    				 				mysqlQuery.getString(mysqlCollumns.MYSQLNAME.ordinal()),
	    				 				mysqlQuery.getString(mysqlCollumns.MYSQLPASS.ordinal()),
	    				 				"X"});
	    		}
	    		 
	        	refreshCustomersTable();
    		 }
    	 catch(Exception e)
    	 {
    		FTPSyncMainWindow.log.logError(e.toString());
    		/*
    		 * If there was error disable all unneeded buttons
    		 */
		    chooseAllCustomers.setEnabled(false);
		    uncheckAllCustomers.setEnabled(false);
		    addNewCustomer.setEnabled(false);
		    editCustomer.setEnabled(false);
		    removeCustomer.setEnabled(false);
		    FTPSyncMainWindow.startStopUploading.setEnabled(false);
    	 }
    }
	
	public static void refreshCustomersTable()
	{
		if(customersTable != null)
		{
			customersTable.updateUI();
		}
	}
	
	public void actionPerformed(ActionEvent a)
	{
		if(a.getSource() == chbh)
		{
			if(!isChecked)
			{
				for (int i = 0; i < FTPSyncMainWindow.customers.size();i++)
				{
					customersTableModel.setValueAt(Boolean.TRUE, i, 1);
					isChecked=true;
				}
			}
			else
			{
				for (int i = 0; i < FTPSyncMainWindow.customers.size();i++)
				{
					customersTableModel.setValueAt(Boolean.FALSE, i, 1);
					isChecked=false;
				}				
			}
		}
		if(a.getSource() == addNewCustomer)
		{
			AddOrEditCustomer addOrEditCustomerFrame = new AddOrEditCustomer();
			addOrEditCustomerFrame.initDialog("ADD");
		}
		if(a.getSource() == editCustomer)
		{
			AddOrEditCustomer addOrEditCustomerFrame = new AddOrEditCustomer();
			addOrEditCustomerFrame.initDialog("EDIT");
		}
		if(a.getSource() == removeCustomer)
		{
			areYouSureDialog();
		}
		if(a.getSource() == buttonOk)
		{
			removeCustomerFromDb();
			areYouSureFrame.setVisible(false);
		}
		if(a.getSource() == buttonStorno)
		{
			areYouSureFrame.setVisible(false);
		}
	}
	
	private void removeCustomerFromDb()
	{
		try
		{
			Connection connection = mysqlConnection();
		 	
    		Statement statement = connection.createStatement();
    		 
    		statement.executeUpdate("DELETE FROM `customers`" +
    								" WHERE `name` = '"+
    								FTPSyncMainWindow.customers.get(
    									customersTable.getSelectedRow())
    										[FTPSyncMainWindow.customersArrayMap.CUSTOMERNAME.ordinal()]
    								+"' LIMIT 1");
    		
    		FTPSyncMainWindow.log.logInfo("Customer "+FTPSyncMainWindow.customers.get(
														customersTable.getSelectedRow())
														[FTPSyncMainWindow.customersArrayMap.CUSTOMERNAME.ordinal()]
														 +" : DELETED");
    		queryCustomersList();
		}
		catch (SQLException e)
		{
			FTPSyncMainWindow.log.logError(e.toString());
		}
	}
	
	private void areYouSureDialog()
	{
		areYouSureFrame = new JFrame();
		JPanel textPanel = new JPanel();
		JPanel okStornoButtonsPanel = new JPanel();

		textPanel.add(new JLabel("Are you sure?"));
		
		okStornoButtonsPanel.setLayout( new GridLayout(1,0));

		buttonOk = new JButton("OK");
		buttonStorno = new JButton("Storno");
		
	    buttonOk.addActionListener(this);
	    buttonStorno.addActionListener(this);
		
		okStornoButtonsPanel.add(buttonOk);
		okStornoButtonsPanel.add(buttonStorno);
		
		areYouSureFrame.add(textPanel);
		areYouSureFrame.add(okStornoButtonsPanel);
		areYouSureFrame.setVisible(true);
		areYouSureFrame.setSize(375,100);
		areYouSureFrame.setLocationRelativeTo(null);
		areYouSureFrame.setLayout(new GridLayout(2,0));
	}

	public static Connection mysqlConnection() throws SQLException
	{
		 Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/"+
															FTPSyncMain.mysqlDb,
															FTPSyncMain.mysqlName,
															FTPSyncMain.mysqlPass);
		 return connection;
	}
}
