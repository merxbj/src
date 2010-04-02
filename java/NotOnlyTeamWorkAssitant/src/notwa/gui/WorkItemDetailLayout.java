package notwa.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ScrollPaneLayout;

public class WorkItemDetailLayout extends JComponent implements ActionListener {
    private static WorkItemDetailLayout instance;
    JTabbedPane detailTabs = new JTabbedPane();
    JButton hideDetail = new JButton("Hide detail");
    private WorkItemDetail wid;
    private WorkItemNoteHistoryTable winht;

    public WorkItemDetailLayout() {
    }
    
    public static WorkItemDetailLayout getInstance() {
        if (instance == null) {
            instance = new WorkItemDetailLayout();
        }
        return instance;
    }
    
    public Component initDetailLayout() {
        this.setLayout(new BorderLayout());
    
        this.add(hideDetail, BorderLayout.PAGE_START);
        hideDetail.addActionListener(this);
        
        detailTabs.addTab("Detail", WorkItemDetail.getInstance().initComponents());
        detailTabs.addTab("Notes history", WorkItemNoteHistoryTable.getInstance().initNoteHistoryTable());

        this.add(detailTabs, BorderLayout.CENTER);
            
        return this;
    }

    public WorkItemDetail getWorkItemDetail() {
        return this.wid;
    }
    
    public WorkItemNoteHistoryTable getWorkItemNoteHistoryTable() {
        return this.winht;
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == hideDetail) {
            MainLayoutLoader.hideDetail();
        }
    }
}
