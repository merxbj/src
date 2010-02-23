package FTPSynchronizer;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import logger.*; // 3t3r's logger instance
/*
 * TODO:multithrething //DONE // check it 
 * TODO:add ignorelist // for files
 */
@SuppressWarnings("serial")
public class FTPSyncMainWindow extends JFrame implements ActionListener
{
	JFrame mainFrame;
	static JTextArea logBox;
	JPanel buttonsPanel,logPanel,secondPanel;
	JButton startStopUploading,buttonExit;
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
    enum LogType {LOG_LEVEL_ERROR, LOG_LEVEL_INFO, LOG_LEVEL_DEBUG}
	
	public static LogDispatcher log;

	/*
	 * Threading test implementation
	 */
    private UploadProcessing threadObject; // class representing the thread
    private Thread workerThread; // class encapsulating the WorkerThread and providing an interface to work with it
    private boolean isRunning;
    /*
     * --end--
     */
	
	public FTPSyncMainWindow()
	{
        log = new LogDispatcher();
        log.registerLogger(new Logger("main.log", "MainModule", new LogExceptionHandlerImpl()));
        
        // create the worker thread
        threadObject = new UploadProcessing(); // create the object representing the thread
        workerThread = new Thread(threadObject); // create the actuall thread
        isRunning = false; // we are not running, yet
	}
	
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
	    logBox = new JTextArea("Welcome to FTP uploader v1.0.0-r1\n", 1, 1);
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

	    startStopUploading = new JButton("Start uploading");
	    buttonExit = new JButton("Exit");
	    
	    startStopUploading.addActionListener(this);
	    buttonExit.addActionListener(this);

	    buttonsPanel.add(startStopUploading);
	    buttonsPanel.add(buttonExit);
	    buttonsPanel.setLayout(new GridLayout(1,0));
	}

	public void actionPerformed(ActionEvent a)
	{
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
            //getLog().append("Going to start the thread\n");
			//startStopUploading.setEnabled(false);
			insertToLog("Uploading process started");
			getChoosenFilesForUpload();
			getChoosenCustomers();
            workerThread.start();
			/*UploadProcessing up = new UploadProcessing();
			up.startUploadProcess(choosenFiles,choosenCustomers);*/
			
			//startStopUploading.setEnabled(true);
            startStopUploading.setText("Stop uploading\n");
            isRunning = true;
            //getLog().append("Thread started\n");
        }
        else
        {
            //getLog().append("Going to stop the thread\n");
			insertToLog("USER ABORT!");
            threadObject.stopThread();
            startStopUploading.setText("Start uploading\n");
            isRunning = false;
            //getLog().append("Thread stopped\n");
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
			insertToLog("Gathering choosen files : DONE", LogType.LOG_LEVEL_INFO);
		}
		else
		{
			insertToLog("Gathering choosen files : FAILED", LogType.LOG_LEVEL_INFO);
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
			insertToLog("Gathering choosen customers : DONE", LogType.LOG_LEVEL_INFO);
		}
		else
		{
			insertToLog("Gathering choosen customers : FAILED", LogType.LOG_LEVEL_INFO);
		}
	}
	
	public static void insertToLog(String text)
	{
		insertToLog(text,LogType.LOG_LEVEL_INFO);
	}
	
	public static void insertToLog(String text,LogType lType)
	{
		switch (lType)
		{
			case LOG_LEVEL_ERROR:
			{
				log.logError(text);
				logBox.insert(log.getMessage(), 0);
				break;
			}
			case LOG_LEVEL_INFO:
			{
				log.logInfo(text);
				logBox.insert(log.getMessage(), 0);
				break;
			}
			case LOG_LEVEL_DEBUG:
			{
				log.logWarning(text);
				logBox.insert(log.getMessage(), 0);
				break;
			}
			default:
			{
				log.logInfo(text);
				logBox.insert(log.getMessage(), 0);
				break;
			}			
	    }
		logBox.update(logBox.getGraphics());
	}	
	
	public static void updateProgressBarValue(int value)
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
