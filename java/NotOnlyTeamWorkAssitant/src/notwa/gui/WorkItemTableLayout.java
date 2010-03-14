package notwa.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import notwa.wom.WorkItemCollection;

@SuppressWarnings("serial")
public class WorkItemTableLayout extends JPanel implements ActionListener {
    JButton addButton,editButton,showHideButton,showDepButton;
    
    public WorkItemTableLayout(WorkItemCollection wic) {
        this.setLayout(new BorderLayout());
        
        JPanel witTable = new WorkItemTable(wic);
        this.add(witTable, BorderLayout.CENTER);
        
        JPanel ib = initButtons();
        this.add(ib, BorderLayout.PAGE_END);
    }
    
    private JPanel initButtons() {
        JPanel buttonsPanel = new JPanel(new BorderLayout());
        
        JPanel leftButtonsPanel = new JPanel();
        JPanel rightButtonsPanel = new JPanel();
        
        addButton = new JButton("Add");
        editButton = new JButton("Edit");
        showHideButton = new JButton("Show/Hide Detail");
        showDepButton = new JButton("Show dependency tree");
        
        addButton.addActionListener(this);
        editButton.addActionListener(this);
        showHideButton.addActionListener(this);
        showDepButton.addActionListener(this);
        
        leftButtonsPanel.add(addButton);
        leftButtonsPanel.add(editButton);
        rightButtonsPanel.add(showHideButton);
        rightButtonsPanel.add(showDepButton);
        
        buttonsPanel.add(leftButtonsPanel, BorderLayout.LINE_START);
        buttonsPanel.add(rightButtonsPanel, BorderLayout.LINE_END);
        
        return buttonsPanel;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if(ae.getSource() == addButton) {
            WorkItemEditor aewitd = new WorkItemEditor();
            aewitd.initAddDialog();
        }
        
        if(ae.getSource() == editButton) {
            WorkItemEditor aewitd = new WorkItemEditor();
            aewitd.initEditDialog();
        }
        
        if(ae.getSource() == showHideButton) {
            TabContent.ShowHideDetail();
        }
    }
}
