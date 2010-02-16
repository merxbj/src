package FTPSynchronizer;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/*
 *TODO: make ENUM for mysql collumns
 *TODO: save log while program is running, not at the end 
 */


@SuppressWarnings("serial")
public class FTPSyncMain extends JFrame implements ActionListener,ListSelectionListener
{
	JFrame mainFrame;
	JList loginsList;
	static JTextArea log;
	JPanel buttonsPanel,topPanel,centerPanel;
	JButton startUploading,chooseAllFiles,uncheckAllFiles,chooseAllCustomers,uncheckAllCustomers,buttonExit;
	JCheckBox filesCheckBox,customersCheckBox;
	JScrollPane scrollPaneFilesTable,scrollPaneCustomersTable,scrollPaneLog;
	JStatusBar statusBar;
	static JTable customersTable;
	JTable filesTable;
	public static JProgressBar progressBar;
	
	Container contentPane;

	private TblModel filesTableModel,customersTableModel;
	
	static ArrayList<Object[]> filesForUpload = new ArrayList<Object[]>();
	static ArrayList<String> choosenFiles = new ArrayList<String>();
	public static ArrayList<Object[]> customers = new ArrayList<Object[]>();
	
	static String mysqlName;
	static String mysqlPass;
	static String mysqlDb;
	public static String rootDir;
	
	public FTPSyncMain()
    {
	    /*
	     * Arranges default layout 
	     */

	    createFilesTable();
	    createCustomersTable();
	    createJPanelWithButtons();
	    createJTextAreaLog();
	    
	    centerPanel = new JPanel();
	    topPanel = new JPanel();

	    centerPanel.setLayout(new BorderLayout());
        centerPanel.add(scrollPaneFilesTable);
        centerPanel.add(scrollPaneCustomersTable, BorderLayout.EAST);    
        centerPanel.add(buttonsPanel, BorderLayout.SOUTH);
        
        topPanel.add(scrollPaneLog);
        topPanel.setLayout(new java.awt.GridLayout(1,0));

	    /*
		 *	Create window with all elements  
		 */

    	mainFrame = new JFrame("FTP - Updater");
    	mainFrame.setSize(1000,500);
    	mainFrame.setLocationRelativeTo(null);
    	mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	    contentPane = mainFrame.getContentPane();
	    contentPane.setLayout(new BorderLayout());

	    contentPane.add(topPanel, BorderLayout.NORTH);
	    contentPane.add(centerPanel, BorderLayout.CENTER);
	    
        createJStatusBarComponent();

    	mainFrame.setVisible(true); 
    	
    }
	
	private void createJTextAreaLog()
    /*
	 *	simple JTextArea to show what we are currently doing  
	 */
	{
	    log = new JTextArea("Welcome to FTP uploader v0.9.9\n", 10, 80);
	    scrollPaneLog = new JScrollPane(log);
	}

	private void createJStatusBarComponent()
	/*
	 * Create StatusBar for progress bar
	 */
	{
	    statusBar = new JStatusBar();
	    contentPane.add(statusBar, BorderLayout.SOUTH);
	    createProgressBar();
	    
	}

	private void createProgressBar()
	{
		progressBar = new JProgressBar();
		progressBar.setPreferredSize(new Dimension(500,50));
		progressBar.setStringPainted(true);
		JStatusBar.rightPanel.add(progressBar);
	}

	private void createFilesTable()
    /*
	 *	Read all files from DIR and create JTable with checkboxes to show and choose them  
	 */
	{
    	File dirRoot = new File(rootDir);

    	
    	
    	File[] files = dirRoot.listFiles();
		readFolderContent(files);

		filesTableModel = new TblModel(filesForUpload);
	    filesTable = new JTable(filesTableModel);
	    filesTable.getColumnModel().getColumn(1).setMaxWidth(20);
	    filesTable.getColumnModel().getColumn(0).setHeaderValue("Soubory");
	    filesTable.getColumnModel().getColumn(1).setHeaderValue("X");
	    
	    JTableCellRenderer tableCellRenderer = new JTableCellRenderer();
	    filesTable.getColumnModel().getColumn(0).setCellRenderer(tableCellRenderer);
	    
	    scrollPaneFilesTable = new JScrollPane(filesTable);
	    TableColumn fileCheckFileColumn = filesTable.getColumnModel().getColumn(1);
	    filesCheckBox = new JCheckBox();
	    fileCheckFileColumn.setCellEditor(new DefaultCellEditor(filesCheckBox));
	}

	private void createCustomersTable()
    /*
	 *	Show table with all customers from mysql table
	 */
	{
		queryCustomersList();

		customersTableModel = new TblModel(customers);
	    customersTable = new JTable(customersTableModel);
	    customersTable.getColumnModel().getColumn(1).setMaxWidth(20);
	    customersTable.getColumnModel().getColumn(0).setHeaderValue("Zakaznici");
	    customersTable.getColumnModel().getColumn(1).setHeaderValue("X");
	    
	    JTableCellRenderer tableCellRenderer = new JTableCellRenderer();
	    customersTable.getColumnModel().getColumn(0).setCellRenderer(tableCellRenderer);
	    
	    scrollPaneCustomersTable = new JScrollPane(customersTable);
	    TableColumn fileCheckCustomerColumn = customersTable.getColumnModel().getColumn(1);
	    customersCheckBox = new JCheckBox();
	    fileCheckCustomerColumn.setCellEditor(new DefaultCellEditor(customersCheckBox));
	}
	
	private void createJPanelWithButtons()
    /*
	 *	Creates "group" object with buttons for working with program  
	 */
	{
	    buttonsPanel = new JPanel();
	    chooseAllFiles = new JButton("Vyber vsechny soubory");
	    uncheckAllFiles = new JButton("Zrus vybrani souboru");
	    chooseAllCustomers = new JButton("Vyber vsechny zakazniky");
	    uncheckAllCustomers = new JButton("Zrus vybrani zakazniku");
	    startUploading = new JButton("Zacni uploadovat");
	    buttonExit = new JButton("exit");
	    
	    chooseAllFiles.addActionListener(this);
	    uncheckAllFiles.addActionListener(this);
	    chooseAllCustomers.addActionListener(this);
	    uncheckAllCustomers.addActionListener(this);
	    startUploading.addActionListener(this);
	    buttonExit.addActionListener(this);

	    buttonsPanel.add(chooseAllFiles);
	    buttonsPanel.add(uncheckAllFiles);
	    buttonsPanel.add(chooseAllCustomers);
	    buttonsPanel.add(uncheckAllCustomers);
	    buttonsPanel.add(startUploading);
	    buttonsPanel.add(buttonExit);
	}

	private void readFolderContent(File[] files)
	/*
	 *  Reads all files from rootDir command line argument
	 */
    {
   		for(int i=0; i < files.length; i++)
   		{
   	    	if(files[i].isDirectory() && (files[i].list().length != 0) && !files[i].getName().substring(0,1).equals("."))
   	    	{ // if it is dir and has something to print
   	    		File[] subDir = files[i].listFiles();
   	    		readFolderContent(subDir);
   	    	}
   	    	else
   	    	{
    			filesForUpload.add(new Object[] {removeWinSlashes(files[i].getAbsolutePath()), false});
   	    	}
   		}
    }

    private void queryCustomersList()
    {
    	/*
    	 *CREATE TABLE IF NOT EXISTS `customers` (
		 * `name` varchar(255) NOT NULL,
		 * `url` varchar(500) NOT NULL,
		 * `connName` varchar(200) NOT NULL,
		 * `connPass` varchar(200) NOT NULL
		 * ) ENGINE=MyISAM DEFAULT CHARSET=utf8;
    	 */
    	 try{
	    		 Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/"+mysqlDb,mysqlName,mysqlPass);
	    		 Statement st = conn.createStatement();
	    		 ResultSet rs = st.executeQuery("SELECT * FROM customers");
	    		 while (rs.next())
	    		 {
	    			 // Last field is ENUM ('E'=Error, 'D'=Done, 'X'=Starting(not yet started)
	    			 customers.add(new Object[]{rs.getString(1),false,rs.getString(2),rs.getString(3),rs.getString(4),"X"});
	    		 }
    		 }
    	 catch(Exception e)
    	 {
    		 System.out.println(e.toString());
    	 }
    }
    
	public void actionPerformed(ActionEvent a)
	{
		if(a.getSource() == chooseAllFiles)
		{
			for (int i = 0; i < filesForUpload.size();i++)
			{
				filesTableModel.setValueAt(Boolean.TRUE, i, 1);
			}
		}
		if(a.getSource() == uncheckAllFiles)
		{
			for (int i = 0; i < filesForUpload.size();i++)
			{
				filesTableModel.setValueAt(Boolean.FALSE, i, 1);
			}
		}
		if(a.getSource() == chooseAllCustomers)
		{
			for (int i = 0; i < customers.size();i++)
			{
				customersTableModel.setValueAt(Boolean.TRUE, i, 1);
			}
		}
		if(a.getSource() == uncheckAllCustomers)
		{
			for (int i = 0; i < customers.size();i++)
			{
				customersTableModel.setValueAt(Boolean.FALSE, i, 1);
			}
		}
		if(a.getSource() == startUploading)
		{
			insertToLog("Uploading process started");

			startUploading.setEnabled(false);
			
			getChoosenFilesForUpload();
			new UploadProcessing(choosenFiles,customers);
			
			startUploading.setEnabled(true);
		}
		if(a.getSource() == buttonExit)
		{
			System.exit(0);
		}
	}

	private void getChoosenFilesForUpload()
	{
		insertToLog("Gathering choosen files");
		for (int i=0; i < filesForUpload.size(); i++)
		{
			if (filesForUpload.get(i)[1].equals(true))
			{
				choosenFiles.add(filesForUpload.get(i)[0].toString());
			}
		}
		insertToLog("DONE");
	}

	private static void getSettingsFromCommandLine(String[] args)
	{
		/*
		 * Read command line args and save them for later use
		 */
		for (int i=0;i < args.length;i++)
		{
			int index = args[i].indexOf('=');

			if(args[i].substring(0, index).equals("--mysqlName"))
			{
				mysqlName=args[i].substring(index+1);
			}
			else if(args[i].substring(0, index).equals("--mysqlPass"))
			{
				mysqlPass=args[i].substring(index+1);
			}
			else if(args[i].substring(0, index).equals("--mysqlDb"))
			{
				mysqlDb=args[i].substring(index+1);
			}
			else if(args[i].substring(0, index).equals("--rootDir"))
			{
				rootDir=args[i].substring(index+1);
			}
		}
		
		/*
		 * Check if rootDir is specified, if not use user home dir
		 */
		if(rootDir.isEmpty())
		{
			rootDir = System.getProperty("user.home");
		}
	}

	public static void insertToLog(String text)
	{
		insertToLog(text,false);
	}
	
	public static void insertToLog(String text,boolean pureText)
	{
		if(pureText)
		{
			log.insert(text+"\n", 0);
		}
		else
		{
			log.insert("====( "+text+" )==========\n", 0);
		}
		log.update(log.getGraphics());
	}	
	
	public void valueChanged(ListSelectionEvent e)
	{
		if (e.getValueIsAdjusting())
		{
			int index = e.getFirstIndex();
			filesTableModel.setValueAt(Boolean.TRUE, index, 0);
		}
	}
	
	public static void updateProgressBarValue(int value)
	{
		progressBar.setValue(value);
		progressBar.update(progressBar.getGraphics());
	}

	public static void main(String[] args)
    {
    	getSettingsFromCommandLine(args);
        new FTPSyncMain();
    }

	public static void refreshCustomersTable()
	{
		customersTable.update(customersTable.getGraphics());
	}
	
	public static String removeWinSlashes(String strForConversion)
	{
		String convertedStr = "";
		for (int i = 0;i<strForConversion.split("\\\\").length;i++)
		{
			if (convertedStr.isEmpty())
			{
				convertedStr = strForConversion.split("\\\\")[i];				
			}
			else
			{
				convertedStr = convertedStr + "/" + strForConversion.split("\\\\")[i];
			}
		}
		//System.out.println(convertedStr+"/");
		return convertedStr;
	}
}
