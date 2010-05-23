package notwa.gui;

import notwa.gui.components.KeyValueComboBox;
import notwa.gui.datamodels.UserManagementModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import notwa.common.ConnectionInfo;
import notwa.dal.UserDal;
import notwa.dal.WorkItemDal;
import notwa.security.Credentials;
import notwa.sql.Parameter;
import notwa.sql.ParameterSet;
import notwa.sql.Parameters;
import notwa.sql.Sql;
import notwa.wom.Context;
import notwa.wom.User;
import notwa.wom.UserCollection;
import notwa.wom.WorkItem;
import notwa.wom.WorkItemCollection;
import notwa.wom.WorkItemStatus;

public class UserManagement extends JDialog implements ActionListener, ListSelectionListener {

    private JButton closeButton, addButton, editButton, delButton;
    private Context context;
    private ConnectionInfo ci;
    private JTable table;
    private JTableCellRenderer tableCellRenderer = new JTableCellRenderer();
    private UserCollection uc;
    private UserDal ud;
    private UserManagementModel tblModel;
    
    public UserManagement(ConnectionInfo ci, Context context) {
        this.context = context;
        this.ci = ci;
        init();
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
        
        tblModel = new UserManagementModel(uc);
        table = new JTable(tblModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setCellRenderer(tableCellRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(tableCellRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(tableCellRenderer);
        table.getSelectionModel().addListSelectionListener(this);
        managementPanel.add(new JScrollPane(table));
                
        return managementPanel;
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
        
        editButton.setEnabled(false);
        delButton.setEnabled(false);
        
        return buttonsPanel;
    }
    
    private User getSelectedUser() {
        int selectedIndex = table.convertRowIndexToModel(table.getSelectedRow());
        return uc.get(selectedIndex);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == addButton) {
            User user = new User();
            user.registerWithContext(context);
            UserEditor ue = new UserEditor(user, true);
            ue.init();
            if (user.isInserted()) {
                uc.add(user);
                ud.update(uc);
                tblModel.fireTableDataChanged();
            }
        }

        if (ae.getSource() == editButton) {
            UserEditor ue = new UserEditor(this.getSelectedUser(), false);
            ue.init();
            ud.update(uc);
            tblModel.fireTableDataChanged();
        }
        
        if (ae.getSource() == delButton) {
            if (JOptionPane.showConfirmDialog(this, "Are you sure?") == 0) {
                KeyValueComboBox<User> cb = new KeyValueComboBox<User>();
                cb.addItem(null, ""); // add an empty user
                for (User user : uc) {
                    if (!user.equals(this.getSelectedUser()))
                        cb.addItem(user, user.getLogin());
                }

                Object[] msg = {"Select which user will take user's work items:", cb};
                int result = JOptionPane.showConfirmDialog(this, msg, "NOTWA - Question", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                if(result == JOptionPane.OK_OPTION) {
                    WorkItemCollection wic = new WorkItemCollection();
                    wic.setCurrentContext(context);
                    WorkItemDal wid = new WorkItemDal(ci, context);
                    wid.fill(wic, getDefaultParameters(this.getSelectedUser()));

                    for (WorkItem wi : wic) {
                        wi.setAssignedUser(cb.getSelectedKey());
                    }
                    
                    wid.update(wic);

                    /*
                     * Delete selected user
                     */
                    this.getSelectedUser().setDeleted(true);
                    ud.update(uc);
                    tblModel.fireTableDataChanged();
                }
            }
        }
        
        if (ae.getSource() == closeButton) {
            this.setVisible(false);
        }
    }
    
    private ParameterSet getDefaultParameters(User user) {
        return new ParameterSet( new Parameter[] { 
            new Parameter(Parameters.WorkItem.ASSIGNED_USER, user.getId(), Sql.Relation.EQUALTY) });
    }
    
    @Override
    public void valueChanged(ListSelectionEvent lse) {
        if (table.getSelectedRow() != -1) {
            editButton.setEnabled(true);
            delButton.setEnabled(true);
        }
        else {
            editButton.setEnabled(false);
            delButton.setEnabled(false);
        }
    }
}