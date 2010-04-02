package notwa.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import notwa.common.ConnectionInfo;
import notwa.wom.WorkItemCollection;

public class TabContent extends JComponent implements ActionListener {
    JButton addButton,showHideButton,showDepButton;
    private ConnectionInfo ci;
    private WorkItemTable wiTable;

    //TODO: create new context menu on every TAB - 1. menu item - Close connection
    //TODO: both must have parameter to know what information we want to show
    public TabContent() {
    }
    
    public TabContent initTabContent(WorkItemCollection wic, ConnectionInfo ci) {
        this.ci = ci;
        this.setLayout(new BorderLayout());
        
        JPanel topPanel = new JPanel(new BorderLayout());
        wiTable = new WorkItemTable(wic);
        topPanel.add(this.initButtons(), BorderLayout.PAGE_START);
        topPanel.add(wiTable, BorderLayout.CENTER);
        
        this.add(topPanel, BorderLayout.CENTER);
        return this;
    }
    
    private void fillDefaultSortingItems(JComboBox jcb) { // TODO
        jcb.addItem("Product");
        jcb.addItem("Status");
        jcb.addItem("Priority");
    }
    
    private void fillUserDefinedFilterItems(JComboBox jcb) { // TODO
        jcb.addItem("Product = notwa");
        jcb.addItem("Status = IN_PROGRESS");
        jcb.addItem("Priority = critical");
    }
    
    private JPanel initButtons() {
        JPanel buttonsPanel = new JPanel(new BorderLayout());
        JPanel leftButtonsPanel = new JPanel();
        
        addButton = new JButton("Add");
        /*TODO showDepButton = new JButton("Show dependency tree");*/
        
        addButton.addActionListener(this);
        
        leftButtonsPanel.add(addButton);
        
        buttonsPanel.add(leftButtonsPanel, BorderLayout.LINE_START);
        buttonsPanel.add(this.initFilteringComboBoxes(), BorderLayout.LINE_END);
        
        return buttonsPanel;
    }
    
    private JPanel initFilteringComboBoxes() {
        JPanel jp = new JPanel();
        
        JComboBox defaultSortBox = new JComboBox();
        JComboBox userDefinedFiltersBox = new JComboBox();
        
        fillDefaultSortingItems(defaultSortBox);
        fillUserDefinedFilterItems(userDefinedFiltersBox);
        
        jp.add(defaultSortBox);
        jp.add(userDefinedFiltersBox);
        
        return jp; 
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if(ae.getSource() == addButton) {
            WorkItemEditor aewitd = new WorkItemEditor();
            aewitd.initAddDialog();
        }
    }
    
    public ConnectionInfo getCurrentConnectionInfo() {
        return ci;
    }

    public WorkItemTable getWorkItemTable() {
        return wiTable;
    }
}
