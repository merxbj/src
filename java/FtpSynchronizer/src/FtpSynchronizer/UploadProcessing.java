package FTPSynchronizer;

import java.io.IOException;
import java.util.ArrayList;

import FTPSynchronizer.FTPSyncMainWindow.customersArrayMap;

public class UploadProcessing implements Runnable
{
	ExtendedFtpClient ftp;
	ArrayList<String> choosenFiles;
	ArrayList<Object[]> choosenCustomers;
	String currentPath;
	
	private boolean stop;
	
	public UploadProcessing()
	{
		this.stop = false;
	}

	public void run()
	{
		startUploadingProcess();
	}

	public synchronized void stopThread()
	{
		this.stop = true;
	}
	
	public void startUploadingProcess()
	{
		/*
		 * Load actual data
		 */
		this.choosenFiles = FTPSyncMainWindow.choosenFiles;
		this.choosenCustomers = FTPSyncMainWindow.choosenCustomers;		

		/*
		 * Count choosen customers for ProgressBar
		 */
		
		FTPSyncMainWindow.progressBar.setMaximum((choosenFiles.size()*choosenCustomers.size())+choosenCustomers.size());
		FTPSyncMainWindow.progressBar.setValue(0);

		/*
		 * Go throw customers and begin FTP connection
		 */
		for (int i=0;i<choosenCustomers.size();i++)
		{
			if(ftpProcessing(	choosenCustomers.get(i)[customersArrayMap.CUSTOMERNAME.ordinal()].toString(),
								choosenCustomers.get(i)[customersArrayMap.URL.ordinal()].toString(),
								choosenCustomers.get(i)[customersArrayMap.SUBDIR.ordinal()].toString(),
								choosenCustomers.get(i)[customersArrayMap.MYSQLNAME.ordinal()].toString(),
								choosenCustomers.get(i)[customersArrayMap.MYSQLPASS.ordinal()].toString()))
			{
				choosenCustomers.get(i)[customersArrayMap.UPLOADSTATUS.ordinal()] = "D";
				CustomersPanel.refreshCustomersTable();
			}
			else
			{
				choosenCustomers.get(i)[customersArrayMap.UPLOADSTATUS.ordinal()] = "E";
				CustomersPanel.refreshCustomersTable();
			}
			FTPSyncMainWindow.updateProgressBarValue(FTPSyncMainWindow.progressBar.getValue()+1);
		}
		
		FTPSyncMainWindow.progressBar.setValue(FTPSyncMainWindow.progressBar.getMaximum());
		
		FTPSyncMainWindow.logBox.setCaretPosition(0); //scroll to top
		
		/*
		 * Stop everything
		 */
		FTPSyncMainWindow.threadObject.stopThread();
		FTPSyncMainWindow.startStopUploading.setText("Start uploading\n");
		FTPSyncMainWindow.isRunning = false;
	}
	
	private boolean ftpProcessing(String customerName, String ftpUrl, String subDir, String ftpName, String ftpPass)
	/*
	 * Connects/disconnects from FTP and start uploading files
	 */
	{
		try
		{
	    	if(!stop) //if stop has not been performed
	    	{	
	    		ftp = new ExtendedFtpClient();
    
			    if(ftp.connect(ftpUrl, ftpName, ftpPass))
			    {
			    	FTPSyncMainWindow.log.logInfo("Connected to "+customerName+" :: "+ftpUrl);
			    	if(!subDir.isEmpty())
			    	{
			    		ftp.changeWorkingDir(subDir); //Change working directory on server
			    	}
			    	if(uploadFiles())
			    	{
					    ftp.disconnect();
					    return true;
			    	}
			    	else
			    	{
					    ftp.disconnect();
			    		return false;
			    	}
			    }
			    else
			    {
			    	FTPSyncMainWindow.log.logError("Cannot connect to "+customerName+" :: "+ftpUrl);
					return false;
			    }
	    	}
	    	else
	    	{
	    		return false;
	    	}
		}
		catch (IOException e)
		{
			FTPSyncMainWindow.log.logError(e.toString());
		    return false;
		}
	}
	
	private boolean uploadFiles()
	/*
	 * Start uploading files
	 */
	{
    	if(!stop) //if stop has not been performed
    	{	
			currentPath = "";
			
			for(int i=0;i<choosenFiles.size();i++)
			{
				try
				{
					currentPath = choosenFiles.get(i).substring(0,choosenFiles.get(i).lastIndexOf("/")+1);
									
					if(ftp.changeWorkingDir(currentPath))
					{
						ftp.storeFile(FTPSyncMain.rootDir + choosenFiles.get(i));
					}
					
					/*
					 * IF we have something next?
					 */
					if(i+1 < choosenFiles.size())
					{
						/*
						 * IF next file hasnt same path like file before
						 */
						if(!currentPath.equals(choosenFiles.get(i+1).substring(0,choosenFiles.get(i+1).lastIndexOf("/"))))
						{
							for (int o=0;o<choosenFiles.get(i).toString().split("/").length-1;o++)
							{
								ftp.changeDirUp();
							}
						}
					}
					
					FTPSyncMainWindow.updateProgressBarValue(FTPSyncMainWindow.progressBar.getValue()+1);
				}
				catch (IOException e)
				{
					FTPSyncMainWindow.log.logError(e.toString());
					return false;
				}
			}
			return true;
    	}
    	else
    	{
    		return false;
    	}
	}
}