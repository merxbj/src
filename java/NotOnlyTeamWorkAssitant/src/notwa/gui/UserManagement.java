package notwa.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import notwa.common.ConnectionInfo;
import notwa.dal.UserDal;
import notwa.wom.Context;
import notwa.wom.UserCollection;

public class UserManagement extends JDialog implements ActionListener {
    private JButton okButton, stornoButton, addButton, editButton;
    private Context context;
    private ConnectionInfo ci;
    private JTable table;
    private JTableCellRenderer tableCellRenderer = new JTableCellRenderer();
    private String[] tableHeaders = new String[]{"Login", "Name", "Last name"};
    
    public UserManagement(ConnectionInfo ci, Context context) {
        this.context = context;
        this.ci = ci;
    }
    public void init() {
        this.setLayout(new BorderLayout());
        this.setTitle("NOTWA - NOT Only Team Work Assistent - User Management");
        this.setSize(500,300);
        this.setModalityType(JDialog.ModalityType.APPLICATION_MODAL);
        this.setLocationRelativeTo(null);
        
        this.add(this.initManagementDialog(), BorderLayout.CENTER);
        this.add(this.initButtons(), BorderLayout.PAGE_END);
        
        this.setVisible(true);
    }
    
    public Component initManagementDialog() {
        JPanel managementPanel = new JPanel(new GridLayout(1,0));
        
        UserDal ud = new UserDal(ci, context);
        UserCollection uc = new UserCollection(context);
        ud.fill(uc);
        
        TblModel tblModel = new TblModel(uc, tableHeaders);
        table = new JTable(tblModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setCellRenderer(tableCellRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(tableCellRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(tableCellRenderer);
        
        managementPanel.add(table);
                
        return new JScrollPane(managementPanel);
    }
    
    private JPanel initButtons() {
        JPanel buttonsPanel = new JPanel();
        
        addButton = new JButton("Add");
        editButton = new JButton("Edit");
        okButton = new JButton("Ok");
        stornoButton = new JButton("Storno");
        
        addButton.addActionListener(this);
        editButton.addActionListener(this);
        okButton.addActionListener(this);
        stornoButton.addActionListener(this);
        
        buttonsPanel.add(addButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(okButton);
        buttonsPanel.add(stornoButton);
        
        return buttonsPanel;
    }
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == addButton) {
            UserEditor ue = new UserEditor(null);
            ue.init();
        }

        if (ae.getSource() == editButton) {
            UserEditor ue = new UserEditor(null);
            ue.init();
        }
        
        if (ae.getSource() == okButton) {
            this.setVisible(false);
        }
        
        if (ae.getSource() == stornoButton) {
            this.setVisible(false);
        }
    }
}