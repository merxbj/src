package FTPSynchronizer;

import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ExtendedFtpClient
{
	FTPClient ftp;

	public boolean connect(String host,String user,String password) throws IOException
	/*
	 * Connects to FTP client
	 */
	{
		ftp = new FTPClient();
		
		ftp.connect(host);
		ftp.login(user,password);

		if(!(ftp.getReplyCode() == 230))
		{
			return false;
		}

		return true; 
	}
	
	public void disconnect()
	/*
	 * Logout user and disconnect from FTP server
	 */
	{
		try
		{
			ftp.logout();
			ftp.disconnect();
			FTPSyncMainWindow.log.logInfo("Disconnected");
		}
		catch (IOException e)
		{
	    	FTPSyncMainWindow.log.logError("Exception occured while disconnecting: "+e);
		}
	}

	public boolean changeWorkingDir(String folders) throws IOException
	/*
	 * go to dir FOLDERS (/main/test/test2/test3/) / if dir does not exist create one
	 */
	{
		for (int i=0;i<folders.split("/").length;i++)
		{
			if (!ftp.changeWorkingDirectory(folders.split("/")[i]))
			{
				if(ftp.makeDirectory(folders.split("/")[i]))
				{
					if(!ftp.changeWorkingDirectory(folders.split("/")[i]))
					{
						FTPSyncMainWindow.log.logError("Create " +folders.split("/")[i]+ " : FAILED");
						return false;						
					}
				}
				else
				{
					FTPSyncMainWindow.log.logError("Create " +folders.split("/")[i]+ " : FAILED");
					return false;
				}
			}
		}
		return true;
	}

	public void changeDirUp()
	/*
	 * like cd ..
	 */
	{
		try
		{
			ftp.cdup();
		}
		catch (IOException e)
		{
			System.out.println("Error occured while cd .. :"+e);
		}
	}

	public void storeFile(String file)
	/*
	 * Save FILE to server
	 */
	{
		try
		{
		    File localFile = new File(file.substring(0,file.lastIndexOf("/")), file.substring(file.lastIndexOf("/")));
		    InputStream localStream = new FileInputStream(localFile);

		    ftp.storeFile(file.substring(file.lastIndexOf("/")+1), localStream);

		    localStream.close();
		    
			if(ftp.getReplyCode() == 226)
			{
				FTPSyncMainWindow.log.logInfo(file.substring(FTPSyncMain.rootDir.length()-1) + " : SUCCESS");
			}
			else
			{
				FTPSyncMainWindow.log.logError(file.substring(FTPSyncMain.rootDir.length()-1) + " : FAILED");				
			}
		}
		catch (IOException e)
		{
			FTPSyncMainWindow.log.logError(file.substring(FTPSyncMain.rootDir.length()-1) + " : FAILED");
			FTPSyncMainWindow.log.logError(e.toString());
		}
	}
}