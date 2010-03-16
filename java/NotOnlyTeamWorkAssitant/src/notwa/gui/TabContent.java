package notwa.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import notwa.wom.WorkItemCollection;

@SuppressWarnings("serial")
public class TabContent extends JComponent implements ActionListener {
    JButton addButton,editButton,showHideButton,showDepButton;
    static JSplitPane sp;

    //TODO: both must have parameter to know what information we want to show
    public TabContent() {
    }
    
    public TabContent initTabContent(WorkItemCollection wic) {
        this.setLayout(new BorderLayout());
        
        JPanel topPanel = new JPanel(new BorderLayout());
        WorkItemTable wiTable = new WorkItemTable(wic);
        topPanel.add(this.initButtons(), BorderLayout.PAGE_START);
        topPanel.add(wiTable, BorderLayout.CENTER);
        
        WorkItemDetail wid = new WorkItemDetail();
        
        sp = new JSplitPane(    JSplitPane.VERTICAL_SPLIT,
                                topPanel, wid);
        sp.setResizeWeight(0.9);
        sp.setContinuousLayout(true);

        this.add(sp, BorderLayout.CENTER);
        return this;
    }
    
    private void fillDefaultSortingItems(JComboBox jcb) {
        jcb.addItem("Product");
        jcb.addItem("Status");
        jcb.addItem("Priority");
    }
    
    private void fillUserDefinedFilterItems(JComboBox jcb) {
        jcb.addItem("Product = notwa");
        jcb.addItem("Status = IN_PROGRESS");
        jcb.addItem("Priority = critical");
    }
    
    public static void hideDetail() {
        //TODO after fullscreen, is detail visible anyway ?!
        sp.setDividerLocation(50000);
    }
    
    private JPanel initButtons() {
        JPanel buttonsPanel = new JPanel(new BorderLayout());
        JPanel leftButtonsPanel = new JPanel();
        
        addButton = new JButton("Add");
        editButton = new JButton("Edit");
        /*TODO showDepButton = new JButton("Show dependency tree");*/
        
        addButton.addActionListener(this);
        editButton.addActionListener(this);
        
        leftButtonsPanel.add(addButton);
        leftButtonsPanel.add(editButton);
        
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
        
        if(ae.getSource() == editButton) {
            WorkItemEditor aewitd = new WorkItemEditor();
            aewitd.initEditDialog();
        }
    }
}
