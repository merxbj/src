package FTPSynchronizer;

public class FTPSyncMain
{
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
				System.out.println(	"Reparsovani argumentu se nepovedlo\n" +
									"zkontrolujte spravny zapis\n" +
									"--mysqlName=prihlasovaciJmeno\n" +
									"--mysqlPass=prihlasovaciHeslo\n" +
									"--mysqlDb=databaze\n" +
									"--rootDir=c:/projekt/ \n" +
									"//pokud parametr rootDir nebude vyplnen pouzije" +
									" se defaultni adresar uzivatele (os independent)");
				System.exit(0);
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
