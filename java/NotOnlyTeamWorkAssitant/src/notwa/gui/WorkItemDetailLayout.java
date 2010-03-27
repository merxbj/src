package notwa.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;

public class WorkItemDetailLayout extends JComponent implements ActionListener {
    JTabbedPane detailTabs = new JTabbedPane();
    JButton hideDetail = new JButton("Hide detail");
    
    public Component initDetailLayout() {
        this.setLayout(new BorderLayout());

        this.add(hideDetail, BorderLayout.PAGE_START);
        hideDetail.addActionListener(this);
        
        detailTabs.addTab("Detail", WorkItemDetail.getInstance().initComponents());
        detailTabs.addTab("Notes history", WorkItemNoteHistoryTable.getInstance().initNoteHistoryTable());
        
        this.add(detailTabs, BorderLayout.CENTER);
        
        return this;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == hideDetail) {
            TabContent.hideDetail();
        }
    }
}
