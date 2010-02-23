package FTPSynchronizer;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;

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
	JTable filesTable;
	private TblModel filesTableModel;
	JScrollPane scrollPanelFilesTable;
	JCheckBox filesCheckBox;
	JButton chooseAllFiles,uncheckAllFiles;
	JPanel buttonsPanel;
	
	public FilesPanel()
	{
		File dirRoot = new File(FTPSyncMain.rootDir);
	
		File[] files = dirRoot.listFiles();
		readFolderContent(files);
	
		filesTableModel = new TblModel(FTPSyncMainWindow.filesForUpload);
	    filesTable = new JTable(filesTableModel);
	    filesTable.getColumnModel().getColumn(1).setMaxWidth(20);
	    filesTable.getColumnModel().getColumn(0).setHeaderValue("Soubory");
	    filesTable.getColumnModel().getColumn(1).setHeaderValue("X");
	    
	    JTableCellRenderer tableCellRenderer = new JTableCellRenderer();
	    filesTable.getColumnModel().getColumn(0).setCellRenderer(tableCellRenderer);
	    
	    /*
	     * Make it Scrollable
	     */
	    scrollPanelFilesTable = new JScrollPane(filesTable);
	    TableColumn fileCheckFileColumn = filesTable.getColumnModel().getColumn(1);
	    filesCheckBox = new JCheckBox();
	    fileCheckFileColumn.setCellEditor(new DefaultCellEditor(filesCheckBox));
	    
	    initButtons();

	    setLayout(new BorderLayout());
	    add(scrollPanelFilesTable, BorderLayout.CENTER);
	    add(buttonsPanel, BorderLayout.PAGE_END);
	}
	
	private void initButtons()
	{
		buttonsPanel = new JPanel();
	    buttonsPanel.setLayout(new GridLayout(1,0));
		
	    chooseAllFiles = new JButton("Check all");
	    uncheckAllFiles = new JButton("unCheck all");
	    
	    chooseAllFiles.addActionListener(this);
	    uncheckAllFiles.addActionListener(this);
	    
	    buttonsPanel.add(chooseAllFiles);
	    buttonsPanel.add(uncheckAllFiles);
	}
	
	private void readFolderContent(File[] files)
	/*
	 *  Reads all files from rootDir command line argument
	 */
    {
   		for(int i=0; i < files.length; i++)
   		{
   	    	if(files[i].isDirectory() && (files[i].list().length != 0) && !files[i].getName().substring(0,1).equals("."))
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
	
    private Object removeRootDirFromPath(String absolutePath)
    /*
     * root dir is absolutely unneeded
     */
    {
		String subdirPath = absolutePath.substring(FTPSyncMain.rootDir.length()); 
		return subdirPath;
	}
    
	public void actionPerformed(ActionEvent a)
	{
		if(a.getSource() == chooseAllFiles)
		{
			for (int i = 0; i < FTPSyncMainWindow.filesForUpload.size();i++)
			{
				filesTableModel.setValueAt(Boolean.TRUE, i, 1);
			}
		}
		if(a.getSource() == uncheckAllFiles)
		{
			for (int i = 0; i < FTPSyncMainWindow.filesForUpload.size();i++)
			{
				filesTableModel.setValueAt(Boolean.FALSE, i, 1);
			}
		}
	}
}
