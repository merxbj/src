package FTPUploader;

import java.util.ArrayList;
//import org.jibble.simpleftp.*;
import java.io.*;


public class uploadProcessing
{
	newFtpClient ftp;
	ArrayList<String> originalChoosenFiles;
	String currentPath;
	boolean isCompleted;

	public uploadProcessing(ArrayList<String> choosenFiles,ArrayList<Object[]> customers)
	{
		originalChoosenFiles = choosenFiles;
		
		isCompleted = false;
		/*
		 * Count choosen customers for ProgressBar
		 */
		int choosenCustomers = 0;
		for (int c=0;c<customers.size();c++)
		{
			if(customers.get(c)[1].equals(true))
			{
				choosenCustomers++;
			}
		}
		
		
		FTPUploader.progressBar.setMaximum((choosenFiles.size()*choosenCustomers)+choosenCustomers);
		FTPUploader.progressBar.setValue(0);
		
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
					FTPUploader.refreshCustomersTable();
				}
				else
				{
					customers.get(i)[5] = "E";
					FTPUploader.refreshCustomersTable();
				}
				FTPUploader.updateProgressBarValue(FTPUploader.progressBar.getValue()+1);
			}
		}
		
		FTPUploader.progressBar.setValue(FTPUploader.progressBar.getMaximum());
		FTPUploader.log.setCaretPosition(0); //scroll to top
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
		    	FTPUploader.insertToLog("Connected to "+customerName+" :: "+ftpUrl);
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
				FTPUploader.insertToLog("Cannot connect to "+customerName+" :: "+ftpUrl);
				return false;
		    }
		}
		catch (IOException e)
		{
		    FTPUploader.insertToLog(e.toString(),true);
		    return false;
		}
	}
	
	private boolean uploadFiles()
	/*
	 * Start uploading files
	 */
	{
		currentPath = "";
		
		for(int i=0;i<originalChoosenFiles.size();i++)
		{
			try
			{
				currentPath = originalChoosenFiles.get(i).substring(0,originalChoosenFiles.get(i).lastIndexOf("/")+1).toString();
								
				if(ftp.cwd(originalChoosenFiles.get(i).substring(0,originalChoosenFiles.get(i).lastIndexOf("/")+1)))
				{
					ftp.storeFile(FTPUploader.rootDir + originalChoosenFiles.get(i).substring(1));
				}
				
				if(i+1 < originalChoosenFiles.size())
				{
					if(!currentPath.equals(originalChoosenFiles.get(i+1).substring(0,originalChoosenFiles.get(i+1).lastIndexOf("/"))))
					{
						for (int o=0;o<originalChoosenFiles.get(i).toString().split("/").length-2;o++)
						{
							ftp.changeDirUp();
						}
					}
				}
				
				FTPUploader.updateProgressBarValue(FTPUploader.progressBar.getValue()+1);
			}
			catch (IOException e)
			{
				FTPUploader.insertToLog(e.toString(), true);
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
		for (int o=0;o<originalChoosenFiles.size();o++)
		{
			if(!(originalChoosenFiles.get(o).length() < FTPUploader.rootDir.length()))
			{
				if(originalChoosenFiles.get(o).substring(0, FTPUploader.rootDir.length()).equals(FTPUploader.rootDir.toString()))
				{
					originalChoosenFiles.set(o, originalChoosenFiles.get(o).substring(FTPUploader.rootDir.length()-1));
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
			out.write(FTPUploader.log.getText());
    	    out.close();
		}
		catch (Exception e)
		{
			System.err.println("Error: " + e.getMessage());
		}
	}
}