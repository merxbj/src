package FTPSynchronizer;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class FTPSyncMainWindow extends JFrame implements ActionListener
{
	JFrame mainFrame;
	static JTextArea log;
	JPanel buttonsPanel,logPanel,secondPanel;
	JButton startUploading,buttonExit;
	JScrollPane scrollPaneLog;
	JStatusBar statusBar;
	public static JProgressBar progressBar;
	
	Container contentPane,tablesContainer;

	static ArrayList<Object[]> filesForUpload = new ArrayList<Object[]>();
	static ArrayList<String> choosenFiles = new ArrayList<String>();
	public static ArrayList<Object[]> customers = new ArrayList<Object[]>();
	static ArrayList<Object[]> choosenCustomers = new ArrayList<Object[]>();
	
	static FileOutputStream fileOutputStream;
	
	enum mysqlCollumns {NULL, CUSTOMERNAME, URL, SUBDIR, MYSQLNAME, MYSQLPASS} //we have to count from 1
	public enum customersArrayMap {CUSTOMERNAME, ISSELECTED, URL, SUBDIR, MYSQLNAME, MYSQLPASS, UPLOADSTATUS}
	enum filesForUploadMap {FILEPATH, ISSELECTED};
	
	public void FTPSyncMainWindowInit()
    {
	    /*
	     * Arranges default layout 
	     */
	    initButtons();
	    createJTextAreaLog();
	    
		JPanel filesPanel = new FilesPanel();
	    JPanel customersPanel = new CustomersPanel();
	    logPanel = new JPanel();
	    
	    /*
		 *	Create window with all elements  
		 */

    	mainFrame = new JFrame("FTPSynchronizer");
    	mainFrame.setSize(1000,500);
    	mainFrame.setLocationRelativeTo(null);
    	mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	    contentPane = mainFrame.getContentPane();
	    contentPane.setLayout(new BorderLayout());
	    
	    logPanel = new JPanel(new BorderLayout());
	    secondPanel = new JPanel(new GridLayout(2,0));
	    
	    logPanel.add(scrollPaneLog, BorderLayout.CENTER);
	    logPanel.add(buttonsPanel, BorderLayout.SOUTH);
	    
	    secondPanel.add(customersPanel);
	    secondPanel.add(logPanel);
	    
	    mainFrame.add(filesPanel, BorderLayout.LINE_START);
	    mainFrame.add(secondPanel, BorderLayout.CENTER);

	    createJStatusBarComponent();

    	mainFrame.setVisible(true); 
    }
	
	private void createJTextAreaLog()
    /*
	 *	simple JTextArea to show what we are currently doing  
	 */
	{
	    log = new JTextArea("Welcome to FTP uploader v1.0.0-r1\n", 1, 1);
	    log.setEditable(false);
	    scrollPaneLog = new JScrollPane(log);
	}

	private void createJStatusBarComponent()
	/*
	 * Create StatusBar for progress bar
	 */
	{
	    statusBar = new JStatusBar();
	    contentPane.add(statusBar, BorderLayout.PAGE_END);
	    createProgressBar();
	    
	}

	private void createProgressBar()
	{
		progressBar = new JProgressBar();
		progressBar.setPreferredSize(new Dimension(500,50));
		progressBar.setStringPainted(true);
		JStatusBar.rightPanel.add(progressBar);
	}

	private void initButtons()
    /*
	 *	Creates "group" object with buttons for working with program  
	 */
	{
	    buttonsPanel = new JPanel();

	    startUploading = new JButton("Start upload");
	    buttonExit = new JButton("Exit");
	    
	    startUploading.addActionListener(this);
	    buttonExit.addActionListener(this);

	    buttonsPanel.add(startUploading);
	    buttonsPanel.add(buttonExit);
	    buttonsPanel.setLayout(new GridLayout(1,0));
	}

	public void actionPerformed(ActionEvent a)
	{
		if(a.getSource() == startUploading)
		{
			insertToLog("Uploading process started");

			startUploading.setEnabled(false);
			
			openLogFile();
			
			getChoosenFilesForUpload();
			getChoosenCustomers();
			UploadProcessing up = new UploadProcessing();
			up.startUploadProcess(choosenFiles,choosenCustomers);
			
			try
			{
				fileOutputStream.close(); // not needed anymore
			}
			catch (IOException e)
			{
				System.out.println(e.toString());
			} 
			
			startUploading.setEnabled(true);
		}
		if(a.getSource() == buttonExit)
		{
			System.exit(0);
		}
	}

	private void openLogFile()
	/*
	 * Open log file to begin streaming output
	 */
	{
		try
		{
		    fileOutputStream = new FileOutputStream ("log.txt", true);
		}
		catch (IOException e)
		{
			System.out.println ("Unable to write to file" + e.toString());
		}
	}

	private void getChoosenFilesForUpload()
	{
		choosenFiles.clear();

		for (int i=0; i < filesForUpload.size(); i++)
		{
			if (filesForUpload.get(i)[filesForUploadMap.ISSELECTED.ordinal()].equals(true))
			{
				choosenFiles.add(filesForUpload.get(i)[filesForUploadMap.FILEPATH.ordinal()].toString());
			}
		}

		if(!choosenFiles.isEmpty())
		{
			insertToLog("Gathering choosen files : DONE", true);
		}
		else
		{
			insertToLog("Gathering choosen files : FAILED", true);
		}
	}

	private void getChoosenCustomers()
	{
		choosenCustomers.clear();

		for (int i=0; i < customers.size(); i++)
		{
			if (customers.get(i)[customersArrayMap.ISSELECTED.ordinal()].equals(true))
			{
				choosenCustomers.add(new Object[]{
									customers.get(i)[customersArrayMap.CUSTOMERNAME.ordinal()],
									null,
									customers.get(i)[customersArrayMap.URL.ordinal()],
									customers.get(i)[customersArrayMap.SUBDIR.ordinal()],
									customers.get(i)[customersArrayMap.MYSQLNAME.ordinal()],
									customers.get(i)[customersArrayMap.MYSQLPASS.ordinal()],
									customers.get(i)[customersArrayMap.UPLOADSTATUS.ordinal()]
									});
			}
		}

		if(!choosenCustomers.isEmpty())
		{
			insertToLog("Gathering choosen customers : DONE", true);
		}
		else
		{
			insertToLog("Gathering choosen customers : FAILED", true);
		}
	}
	
	public static void insertToLog(String text)
	{
		insertToLog(text,false);
	}
	
	public static void insertToLog(String text,boolean pureText)
	{
		DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy @ HH:mm:ss");
		Date currentDate = new Date();
		
		if(pureText)
		{
			String output = new String(dateFormat.format(currentDate) + " :: " + text + "\n");
			log.insert(output,0);
			if(fileOutputStream != null)
			{
				new PrintStream(fileOutputStream).println (output);
			}
		}
		else
		{
			String output = new String("====( " + dateFormat.format(currentDate) + " )=( " + text + "\n");
			log.insert(output, 0);
			if(fileOutputStream != null)
			{
				new PrintStream(fileOutputStream).println (output);
			}
		}
		log.update(log.getGraphics());
	}	
	
	public static void updateProgressBarValue(int value)
	{
		progressBar.setValue(value);
		progressBar.update(progressBar.getGraphics());
	}
}
