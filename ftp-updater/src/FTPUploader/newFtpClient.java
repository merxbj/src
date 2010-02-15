package FTPUploader;

import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class newFtpClient
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
	    	FTPUploader.insertToLog("Disconnected");
		}
		catch (IOException e)
		{
	    	FTPUploader.insertToLog("Exception occured while disconnecting: "+e);
		}
	}

	public boolean cwd(String folders) throws IOException
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
						FTPUploader.insertToLog("Create " +folders.split("/")[i]+ " : FAILED", true);
						return false;						
					}
				}
				else
				{
					FTPUploader.insertToLog("Create " +folders.split("/")[i]+ " : FAILED", true);
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
				FTPUploader.insertToLog(file.substring(FTPUploader.rootDir.length()-1) + " : SUCCESS", true);
			}
			else
			{
				FTPUploader.insertToLog(file.substring(FTPUploader.rootDir.length()-1) + " : FAILED", true);				
			}
		}
		catch (IOException e)
		{
			FTPUploader.insertToLog(file.substring(FTPUploader.rootDir.length()-1) + " : FAILED", true);
			FTPUploader.insertToLog(e.toString(), true);
		}
	}
}