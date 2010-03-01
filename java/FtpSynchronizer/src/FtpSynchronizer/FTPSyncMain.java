package FTPSynchronizer;

public class FTPSyncMain
{
	final static String version = new String("v1.0.1-r1");
	static String mysqlName;
	static String mysqlPass;
	static String mysqlDb;
	static String rootDir;
	
	private static void getSettingsFromCommandLine(String[] args)
	{
		/*
		 * Read command line args and save them for later use
		 */
		for (int i=0;i < args.length;i++)
		{
			int index = args[i].indexOf('=');
			try
			{
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
			catch (Exception e)
			{
				System.out.println(	"Reparsing command line args has returned error\n" +
									"please check if they are correctly writen\n" +
									"example\n" +
									"--mysqlName=mysqlUserName\n" +
									"--mysqlPass=mysqlUserPassword\n" +
									"--mysqlDb=mysqlDatabase\n" +
									"--rootDir=c:/_project_directory/project/ \n" +
									"//if roodDir will be empty program will use" +
									"default user home dir (os independent)");
				System.exit(0);
			}
		}
		
		/*
		 * Check if rootDir is specified, if not use user home dir
		 */
		try
		{
			if(rootDir.isEmpty())
			{
				rootDir = System.getProperty("user.home");
			}
		}
		catch (Exception e)
		{
			rootDir = System.getProperty("user.home");
		}
	}

	public static void main(String[] args)
    {
    	getSettingsFromCommandLine(args);
    	java.awt.EventQueue.invokeLater(new Runnable()
	    	{
	            public void run()
	            {
	            	FTPSyncMainWindow FTPSyncMW = new FTPSyncMainWindow();
	            	FTPSyncMW.FTPSyncMainWindowInit();
	            }
	        });
    }
}
