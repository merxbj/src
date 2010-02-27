package FTPSynchronizer;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

@SuppressWarnings("serial")
public class FilesPanel extends JPanel implements ActionListener
{
	static JTable filesTable;
	private TblModel filesTableModel;
	JScrollPane scrollPanelFilesTable;
	JCheckBox filesCheckBox;
	JButton chooseAllFiles,uncheckAllFiles,ignoreList;
	
	CheckBoxHeader chbh;
	boolean isChecked;
	
	static String loadedIgnoreList;
	static File[] files;
	
	public FilesPanel()
	{
		File dirRoot = new File(FTPSyncMain.rootDir);
	
		files = dirRoot.listFiles();
		readFolderContent(files);
	
		filesTableModel = new TblModel(FTPSyncMainWindow.filesForUpload);
	    filesTable = new JTable(filesTableModel);
	    filesTable.getColumnModel().getColumn(1).setMaxWidth(20);
	    filesTable.getColumnModel().getColumn(0).setHeaderValue("Soubory");
	    
	    chbh = new CheckBoxHeader();
	    chbh.addActionListener(this);
	    filesTable.getColumnModel().getColumn(1).setHeaderRenderer(chbh);
	    isChecked = false;

	    JTableCellRenderer tableCellRenderer = new JTableCellRenderer();
	    filesTable.getColumnModel().getColumn(0).setCellRenderer(tableCellRenderer);
	    
	    /*
	     * Make it Scrollable
	     */
	    scrollPanelFilesTable = new JScrollPane(filesTable);
	    TableColumn fileCheckFileColumn = filesTable.getColumnModel().getColumn(1);
	    filesCheckBox = new JCheckBox();
	    fileCheckFileColumn.setCellEditor(new DefaultCellEditor(filesCheckBox));

	    setLayout(new BorderLayout());
	    add(scrollPanelFilesTable, BorderLayout.CENTER);
	}
	
	private static void readFolderContent(File[] files)
	/*
	 *  Reads all files from rootDir command line argument
	 */
    {
		loadedIgnoreList = "";
		loadedIgnoreList = getIgnoreListFromFile();
		
   		for(int i=0; i < files.length; i++)
   		{
	    	if(isInIgnoreList(files[i].getName())) // ignoreList
   	    	{
	   	    	if(files[i].isDirectory() && (files[i].list().length != 0))
	   	    	{ // if it is dir and has something to print
	   	    			File[] subDir = files[i].listFiles();
	   	    			readFolderContent(subDir);
	   	    	}
	   	    	else
	   	    	{
	   	    		FTPSyncMainWindow.filesForUpload.add(new Object[]{
	    									removeRootDirFromPath(
	    									removeWinSlashes(
	    									files[i].getAbsolutePath()
	    									)), false});
	   	    	}
   	    	}
   		}
    }
	
	private static boolean isInIgnoreList(String name)
	{
		for (int i=0;i<loadedIgnoreList.split(";").length;i++)
		{
			if(name.toLowerCase().matches(loadedIgnoreList.split(";")[i].toLowerCase()))
			return false;
		}
		return true;
	}
	
	public static void refreshFilesTable()
	{
		if(filesTable != null)
		{
			FTPSyncMainWindow.filesForUpload.clear();
			readFolderContent(files);
			filesTable.updateUI();
		}
	}

	public static String removeWinSlashes(String strForConversion)
	/*
	 * Removes windows stupid double slashes and replace them with / - java knows what to do next itself
	 */
	{
		String convertedStr = "";
		for (int i = 0;i<strForConversion.split("\\\\").length;i++)
		{
			if (convertedStr.isEmpty())
			{
				convertedStr = strForConversion.split("\\\\")[i];				
			}
			else
			{
				convertedStr = convertedStr + "/" + strForConversion.split("\\\\")[i];
			}
		}
		return convertedStr;
	}
	
    private static Object removeRootDirFromPath(String absolutePath)
    /*
     * root dir is absolutely unneeded
     */
    {
		String subdirPath = absolutePath.substring(FTPSyncMain.rootDir.length()); 
		return subdirPath;
	}
    
	public void actionPerformed(ActionEvent a)
	{
		if(a.getSource() == chbh)
		{
			if(!isChecked)
			{
				for (int i = 0; i < FTPSyncMainWindow.filesForUpload.size();i++)
				{
					filesTableModel.setValueAt(Boolean.TRUE, i, 1);
				}
				isChecked = true;
			}
			else
			{
				for (int i = 0; i < FTPSyncMainWindow.filesForUpload.size();i++)
				{
					filesTableModel.setValueAt(Boolean.FALSE, i, 1);
				}
				isChecked = false;
			}

		}
	}
	
	public static String getIgnoreListFromFile()
	{
		String ignoreList = ""; 
		try
		{
			FileInputStream fstream = new FileInputStream("ignorelist");
    	    DataInputStream in = new DataInputStream(fstream);
    	    BufferedReader br = new BufferedReader(new InputStreamReader(in));
    	    String strLine;

    	    while ((strLine = br.readLine()) != null)
    	    {
    	    	ignoreList = ignoreList + strLine;
    	    }

    	    in.close();
		}
		catch (Exception e)
		{
			//error // we dont care
			//probably file does not exist, yet
		}
		return ignoreList;
	}
}