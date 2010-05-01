package notwa.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import notwa.common.ConnectionInfo;
import notwa.dal.UserDal;
import notwa.exception.ContextException;
import notwa.logger.LoggingFacade;
import notwa.wom.Context;
import notwa.wom.User;
import notwa.wom.UserCollection;

public class UserManagement extends JDialog implements ActionListener {
    private JButton closeButton, addButton, editButton, delButton;
    private Context context;
    private ConnectionInfo ci;
    private JTable table;
    private JTableCellRenderer tableCellRenderer = new JTableCellRenderer();
    private String[] tableHeaders = new String[]{"Login", "Name", "Last name"};
    private UserCollection uc;
    private UserDal ud;
    
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
        
        ud = new UserDal(ci, context);
        uc = new UserCollection(context);
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
        delButton = new JButton("Delete");
        closeButton = new JButton("Close");
        
        addButton.addActionListener(this);
        editButton.addActionListener(this);
        delButton.addActionListener(this);
        closeButton.addActionListener(this);
        
        buttonsPanel.add(addButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(delButton);
        buttonsPanel.add(closeButton);
        
        return buttonsPanel;
    }
    
    private User getSelectedUser() {
        Object element = this.table.getValueAt(table.getSelectedRow(), 0);
        return (User)((JListItemCreator)element).getAttachedObject();
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == addButton) {
            User user = new User();
            user.registerWithContext(context);
            UserEditor ue = new UserEditor(user, true);
            ue.init();
            uc.add(user);
            ud.update(uc);
            table.revalidate();
        }

        if (ae.getSource() == editButton) {
            UserEditor ue = new UserEditor(this.getSelectedUser(), false);
            ue.init();
            ud.update(uc);
            table.revalidate();
        }
        
        if (ae.getSource() == delButton) {
            if (JOptionPane.showConfirmDialog(this, "Are you sure?") == 0) {
                //TODO del operation
            }
        }
        
        if (ae.getSource() == closeButton) {
            this.setVisible(false);
        }
    }
}