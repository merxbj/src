package FTPSynchronizer;

import java.util.ArrayList;
//import org.jibble.simpleftp.*;
import java.io.*;


public class UploadProcessing
{
	newFtpClient ftp;
	ArrayList<String> choosenFiles;
	String currentPath;
	
	boolean isCompleted;

	public UploadProcessing(ArrayList<String> choosenFiles,ArrayList<Object[]> customers)
	{
		this.choosenFiles = choosenFiles;
		
		isCompleted = false;
		/*
		 * Count choosen customers for ProgressBar
		 */
		int countChoosenCustomers = 0;
		for (int c=0;c<customers.size();c++)
		{
			if(customers.get(c)[1].equals(true))
			{
				countChoosenCustomers++;
			}
		}
		
		
		FTPSyncMain.progressBar.setMaximum((choosenFiles.size()*countChoosenCustomers)+countChoosenCustomers);
		FTPSyncMain.progressBar.setValue(0);
		
		eraseRootDirFromPath();

		/*
		 * Go throw customers and begin FTP connection
		 */
		for (int i=0;i<customers.size();i++)
		{
			if(customers.get(i)[1].equals(true))
			{
				if(ftpProcessing(	customers.get(i)[0].toString(),
									customers.get(i)[2].toString(),
									customers.get(i)[3].toString(),
									customers.get(i)[4].toString()))
				{
					customers.get(i)[5] = "D";
					FTPSyncMain.refreshCustomersTable();
				}
				else
				{
					customers.get(i)[5] = "E";
					FTPSyncMain.refreshCustomersTable();
				}
				FTPSyncMain.updateProgressBarValue(FTPSyncMain.progressBar.getValue()+1);
			}
		}
		
		FTPSyncMain.progressBar.setValue(FTPSyncMain.progressBar.getMaximum());
		FTPSyncMain.log.setCaretPosition(0); //scroll to top
		saveLogToFile();
	}
	
	private boolean ftpProcessing(String customerName, String ftpUrl, String ftpName, String ftpPass)
	/*
	 * Connect and disconnect from FTP and start uploading files
	 */
	{
		try
		{
		    ftp = new newFtpClient();
		    
		    if(ftp.connect(ftpUrl, ftpName, ftpPass))
		    {
		    	FTPSyncMain.insertToLog("Connected to "+customerName+" :: "+ftpUrl);
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
		    	FTPSyncMain.insertToLog("Cannot connect to "+customerName+" :: "+ftpUrl);
				return false;
		    }
		}
		catch (IOException e)
		{
			FTPSyncMain.insertToLog(e.toString(),true);
		    return false;
		}
	}
	
	private boolean uploadFiles()
	/*
	 * Start uploading files
	 */
	{
		currentPath = "";
		
		for(int i=0;i<choosenFiles.size();i++)
		{
			try
			{
				currentPath = choosenFiles.get(i).substring(0,choosenFiles.get(i).lastIndexOf("/")+1);
								
				if(ftp.changeWorkingDir(currentPath))
				{
					ftp.storeFile(FTPSyncMain.rootDir + choosenFiles.get(i).substring(1));
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
						for (int o=0;o<choosenFiles.get(i).toString().split("/").length-2;o++)
						{
							ftp.changeDirUp();
						}
					}
				}
				
				FTPSyncMain.updateProgressBarValue(FTPSyncMain.progressBar.getValue()+1);
			}
			catch (IOException e)
			{
				FTPSyncMain.insertToLog(e.toString(), true);
				return false;
			}
		}
		return true;
	}

	private void eraseRootDirFromPath()
	/*
	 * Erase rootDir from path string
	 */
	{
		for (int o=0;o<choosenFiles.size();o++)
		{
			if(!(choosenFiles.get(o).length() < FTPSyncMain.rootDir.length()))
			{
				if(choosenFiles.get(o).substring(0, FTPSyncMain.rootDir.length()).equals(FTPSyncMain.rootDir.toString()))
				{
					choosenFiles.set(o, choosenFiles.get(o).substring(FTPSyncMain.rootDir.length()-1));
				}
			}
		}
	}

	private void saveLogToFile()
	/*
	 * Make file log.txt and fill with actual output of log TextArea
	 */
	{
		try
		{
			FileWriter fstream = new FileWriter("log.txt");
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(FTPSyncMain.log.getText());
    	    out.close();
		}
		catch (Exception e)
		{
			System.err.println("Error: " + e.getMessage());
		}
	}
}