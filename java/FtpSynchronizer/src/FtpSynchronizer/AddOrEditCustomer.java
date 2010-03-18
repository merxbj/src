package FTPSynchronizer;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import FTPSynchronizer.FTPSyncMainWindow.mysqlCollumns;

@SuppressWarnings("serial")
public class AddOrEditCustomer extends JFrame implements ActionListener
{
	JButton buttonOk,buttonStorno;
	JTextField cName,cUrl,cSubDir,cConnName;
	JPasswordField cConnPass;
	JPanel buttonsPanel,boxesPanel;
	JLabel lCustomerName,lUrl,lSubDir,lConnName,lConnPass;
	
	String dialogType,originalName,originalUrl,originalSubdir,originalConnName,originalConnPass;
	
	public void initDialog(String dialogType)
	{
		this.dialogType = dialogType;
		
		if(dialogType.equals("ADD"))
		{
			setTitle("Create new customer");
		}
		else if(dialogType.equals("EDIT"))
		{
			setTitle("Edit existing customer");
		}
		setSize(350,175);
		setLocationRelativeTo(null);
   	
    	setLayout(new BorderLayout());
    	
		initEditBoxes();
    	initButtons();
    	add(boxesPanel, BorderLayout.NORTH);
    	add(buttonsPanel, BorderLayout.SOUTH);
    	
    	setVisible(true); 
	}
	
	private void initButtons()
	{
		buttonsPanel = new JPanel();
		buttonsPanel.setLayout( new GridLayout(1,0));

		buttonOk = new JButton("OK");
		buttonStorno = new JButton("Storno");
		
	    buttonOk.addActionListener(this);
	    buttonStorno.addActionListener(this);
		
		buttonsPanel.add(buttonOk);
		buttonsPanel.add(buttonStorno);
	}
	
	private void initEditBoxes()
	{
		boxesPanel = new JPanel();
		boxesPanel.setLayout( new GridLayout(0,2));
		
    	lCustomerName = new JLabel("Customer:");
    	cName = new JTextField();
    	lUrl = new JLabel("URL:");
    	cUrl = new JTextField();
    	lSubDir = new JLabel("Sub directory:");
    	cSubDir = new JTextField();
    	lConnName = new JLabel("Connection name:");
    	cConnName = new JTextField();
    	lConnPass = new JLabel("Connection pass:");
    	cConnPass = new JPasswordField();
    	
    	boxesPanel.add(lCustomerName);
    	boxesPanel.add(cName);
    	boxesPanel.add(lUrl);
    	boxesPanel.add(cUrl);
    	boxesPanel.add(lSubDir);
    	boxesPanel.add(cSubDir);
    	boxesPanel.add(lConnName);
    	boxesPanel.add(cConnName);
    	boxesPanel.add(lConnPass);
    	boxesPanel.add(cConnPass);
    	
    	if(dialogType.equals("EDIT"))
    	{
    		getCustomerSettings();
    	}
	}
	
	private void addNewCustomerToMysql()
	{
		try
		{
			Connection connection = CustomersPanel.mysqlConnection();
			Statement statement = connection.createStatement();
				
			statement.executeUpdate("INSERT INTO `customers` (`name`, `url`, `subdir`, `connName`, `connPass`)" +
									" VALUES ('"+cName.getText()+"'," +
											" '"+cUrl.getText()+"'," +
											" '"+cSubDir.getText()+"'," +
											" '"+cConnName.getText()+"'," +
											" '"+new String(cConnPass.getPassword())+"')");
			FTPSyncMainWindow.log.logInfo("Customer " + cName.getText() + " succesfully added to table");
			CustomersPanel.queryCustomersList();
		}
		catch(Exception e)
		{
			FTPSyncMainWindow.log.logError(e.toString());
		}
	}
	
	public void getCustomerSettings()
    {
		try
		{
    		 	Connection connection = CustomersPanel.mysqlConnection();
	    		Statement statement = connection.createStatement();
	    		ResultSet mysqlQuery = statement.executeQuery("SELECT * FROM customers" +
	    								" WHERE name='"+FTPSyncMainWindow.customers.get(
	    								CustomersPanel.customersTable.getSelectedRow())
	    								[FTPSyncMainWindow.customersArrayMap.CUSTOMERNAME.ordinal()]+"'");
	    		while (mysqlQuery.next())
	    		{
	    			/*
	    			 * Gather all actual settings
	    			 */
					cName.setText(mysqlQuery.getString(mysqlCollumns.CUSTOMERNAME.ordinal()));
	 				cUrl.setText(mysqlQuery.getString(mysqlCollumns.URL.ordinal()));
	 				cSubDir.setText(mysqlQuery.getString(mysqlCollumns.SUBDIR.ordinal()));
	 				cConnName.setText(mysqlQuery.getString(mysqlCollumns.MYSQLNAME.ordinal()));
	 				cConnPass.setText("|_PASSWORD_PROTECTED_|");
	 				originalName = mysqlQuery.getString(mysqlCollumns.CUSTOMERNAME.ordinal());
	 				originalUrl = mysqlQuery.getString(mysqlCollumns.URL.ordinal());
	 				originalSubdir = mysqlQuery.getString(mysqlCollumns.SUBDIR.ordinal());
	 				originalConnName = mysqlQuery.getString(mysqlCollumns.MYSQLNAME.ordinal());
	 				originalConnPass = mysqlQuery.getString(mysqlCollumns.MYSQLPASS.ordinal());
	    		}
    		 }
    	 catch(Exception e)
    	 {
    		FTPSyncMainWindow.log.logError(e.toString());
    		setVisible(false);
    	 }
    }
	
	public void updateCustomerSettings()
    {
		try
		{
			Connection connection = CustomersPanel.mysqlConnection();
			Statement statement = connection.createStatement();
			
			statement.executeUpdate("UPDATE `customers` SET " +
									"`name`='"+cName.getText()+"'," +
									"`url`='"+cUrl.getText()+"'," +
									"`subdir`='"+cSubDir.getText()+"'," +
									"`connName`='"+cConnName.getText()+"'," +
									"`connPass`='"+new String(cConnPass.getPassword())+"' " +
									"WHERE `name`='"+originalName+"'");
			FTPSyncMainWindow.log.logInfo("Customer " + cName.getText() + " succesfully updated");
			CustomersPanel.queryCustomersList();
		}
		catch(Exception e)
		{
			FTPSyncMainWindow.log.logError(e.toString());
		}
    }
	
	public void actionPerformed(ActionEvent a)
	{
		if(a.getSource() == buttonOk)
		{
			if(dialogType.equals("ADD"))
			{
				addNewCustomerToMysql();
			}
			else if(dialogType.equals("EDIT"))
			{
				if(	!cName.getText().equals(originalName) ||
					!cUrl.getText().equals(originalUrl) ||
					!cSubDir.getText().equals(originalSubdir)||
					!cConnName.getText().equals(originalConnName)||
					(
							!new String(cConnPass.getPassword()).equals(originalConnPass) &&
							!new String(cConnPass.getPassword()).equals("|_PASSWORD_PROTECTED_|")))
				{
					updateCustomerSettings();
				}
			}
			setVisible(false);
		}
		
		if(a.getSource() == buttonStorno)
		{
			setVisible(false);
		}
	}
}
