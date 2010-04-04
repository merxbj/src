package notwa.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import notwa.common.ConnectionInfo;
import notwa.wom.WorkItemCollection;

public class TabContent extends JComponent implements ActionListener {
    JButton addButton,showHideButton,showDepButton;
    private ConnectionInfo ci;
    private WorkItemTable wiTable;
    private JComboBox userDefinedFiltersBox, defaultSortBox;

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
        jcb.addItem("Configure ..."); //TODO same ref as mainMenu>Configure sort/filter
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
        
        defaultSortBox = new JComboBox();
        defaultSortBox.addActionListener(this);
        userDefinedFiltersBox = new JComboBox();
        userDefinedFiltersBox.addActionListener(this);
        
        fillDefaultSortingItems(defaultSortBox);
        fillUserDefinedFilterItems(userDefinedFiltersBox);
        
        jp.add(defaultSortBox);
        jp.add(userDefinedFiltersBox);
        
        return jp; 
    }
    
    public ConnectionInfo getCurrentConnectionInfo() {
        return ci;
    }

    public WorkItemTable getWorkItemTable() {
        return wiTable;
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if(ae.getSource() == addButton) {
            WorkItemEditor wie = new WorkItemEditor();
            wie.initAddDialog();
        }
        
        if(ae.getSource() == userDefinedFiltersBox) {
            if(userDefinedFiltersBox.getSelectedItem().equals("Configure ...")) {
                FilteringDialog fd = new FilteringDialog();
                fd.initFilteringDialog();
            }
        }
    }
}
