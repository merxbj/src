package FTPSynchronizer;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import logger.*; // 3t3r's logger instance

@SuppressWarnings("serial")
public class FTPSyncMainWindow extends JFrame implements ActionListener,Observer
{
	JFrame mainFrame;
	static JTextArea logBox;
	JPanel buttonsPanel,logPanel,rightPanel,filesPanel,customersPanel;
	JButton buttonExit, ignoreList;
	public static JButton startStopUploading;
	JScrollPane scrollPaneLog;
	JStatusBar statusBar;
	public static JProgressBar progressBar;
	
	Container contentPane;

	static ArrayList<Object[]> filesForUpload = new ArrayList<Object[]>();
	static ArrayList<String> choosenFiles = new ArrayList<String>();
	public static ArrayList<Object[]> customers = new ArrayList<Object[]>();
	static ArrayList<Object[]> choosenCustomers = new ArrayList<Object[]>();
	
	enum mysqlCollumns {NULL, CUSTOMERNAME, URL, SUBDIR, MYSQLNAME, MYSQLPASS} //we have to count from 1
	enum customersArrayMap {CUSTOMERNAME, ISSELECTED, URL, SUBDIR, MYSQLNAME, MYSQLPASS, UPLOADSTATUS}
	enum filesForUploadMap {FILEPATH, ISSELECTED};
	
	public static LogDispatcher log;

    static UploadProcessing threadObject; // class representing the thread
    private Thread workerThread; // class encapsulating the WorkerThread and providing an interface to work with it
    static boolean isRunning;
	
	public FTPSyncMainWindow()
	{
        log = new LogDispatcher();
        log.registerLogger(new Logger("main.log", "MainModule", new LogExceptionHandlerImpl()));
        log.registerLogger(this);
	}
	
	public void FTPSyncMainWindowInit()
    {
	    /*
	     * Arranges default layout 
	     */
	    initButtons();
	    createJTextAreaLog();
	    
		filesPanel = new FilesPanel();
	    customersPanel = new CustomersPanel();
	    logPanel = new JPanel();
	    
	    /*
		 *	Create window with all elements  
		 */

    	mainFrame = new JFrame("FTPSynchronizer "+FTPSyncMain.version);
    	mainFrame.setSize(1000,500);
    	mainFrame.setLocationRelativeTo(null);
    	mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	    contentPane = mainFrame.getContentPane();
	    contentPane.setLayout(new BorderLayout());
	    
	    logPanel = new JPanel(new BorderLayout());
	    rightPanel = new JPanel(new GridLayout(2,0));
	    
	    logPanel.add(scrollPaneLog, BorderLayout.CENTER);
	    logPanel.add(buttonsPanel, BorderLayout.SOUTH);
	    
	    rightPanel.add(customersPanel);
	    rightPanel.add(logPanel);
	    
	    mainFrame.add(filesPanel, BorderLayout.LINE_START);
	    mainFrame.add(rightPanel, BorderLayout.CENTER);

	    createJStatusBarComponent();

    	mainFrame.setVisible(true); 
    }
	
	private void createJTextAreaLog()
    /*
	 *	simple JTextArea to show what we are currently doing  
	 */
	{
	    logBox = new JTextArea("Welcome to FTP uploader "+FTPSyncMain.version+"\n", 1, 1);
	    logBox.setEditable(false);
	    scrollPaneLog = new JScrollPane(logBox);
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

	    ignoreList = new JButton("Edit ignore list");
	    startStopUploading = new JButton("Start uploading");
	    buttonExit = new JButton("Exit");
	    
	    ignoreList.addActionListener(this);
	    startStopUploading.addActionListener(this);
	    buttonExit.addActionListener(this);

	    buttonsPanel.add(ignoreList);
	    buttonsPanel.add(startStopUploading);
	    buttonsPanel.add(buttonExit);
	    buttonsPanel.setLayout(new GridLayout(1,0));
	}

	public void actionPerformed(ActionEvent a)
	{
		if(a.getSource() == ignoreList)
		{
			IgnoreList il = new IgnoreList();
			il.createIgnoreListWindow();
		}
		if(a.getSource() == startStopUploading)
		{
			startStopButtonActionPerformed();
		}
		if(a.getSource() == buttonExit)
		{
			System.exit(0);
		}
	}
	
    private void startStopButtonActionPerformed()
    {
        if (!isRunning)
        {
            // create the worker thread
            threadObject = new UploadProcessing(); // create the object representing the thread
            workerThread = new Thread(threadObject); // create the actuall thread        
            isRunning = false; // we are not running, yet
            
            
        	log.logInfo("Uploading process started");
			getChoosenFilesForUpload();
			getChoosenCustomers();
			workerThread.start();
            startStopUploading.setText("Stop uploading\n");
            isRunning = true;
        }
        else
        {
            threadObject.stopThread();
            startStopUploading.setText("Start uploading\n");
            isRunning = false;
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
			log.logInfo("Gathering choosen files : DONE");
		}
		else
		{
			log.logError("Gathering choosen files : FAILED");
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
			log.logInfo("Gathering choosen customers : DONE");
		}
		else
		{
			log.logError("Gathering choosen customers : FAILED");
		}
	}

	@Override
	public void update(Observable o, Object arg1)
	{
        if (o instanceof LogDispatcher)
        {
            LogDispatcher ld = (LogDispatcher) o;
			logBox.insert(ld.getFormattedMessage(), 0);
        }
	}
	
	public static synchronized void updateProgressBarValue(int value)
	{
		progressBar.setValue(value);
		progressBar.update(progressBar.getGraphics());
	}
	
    private static class LogExceptionHandlerImpl implements LoggingExceptionHandler
    {
        public void loggingExceptionOccured(Exception ex)
        {
            System.out.println(ex.toString());
        }
    }
}
