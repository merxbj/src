package notwa.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import notwa.common.ConnectionInfo;
import notwa.dal.ProjectDal;
import notwa.gui.tablemodels.ProjectManagementModel;
import notwa.wom.Context;
import notwa.wom.Project;
import notwa.wom.ProjectCollection;
import notwa.wom.User;

public class ProjectManagement extends JDialog implements ActionListener, ListSelectionListener {
    private ConnectionInfo ci;
    private Context context;
    private JButton closeButton;
    private JTable table;
    private ProjectManagementModel tblModel;
    private ProjectDal pd;
    private ProjectCollection pc;
    private JTableCellRenderer tableCellRenderer = new JTableCellRenderer();
    private AbstractButton addButton;
    private JButton assignmentMngr;

    public ProjectManagement(ConnectionInfo ci, Context context) {
        this.ci = ci;
        this.context = context;
        init();
    }
    
    public void init() {
        this.setLayout(new BorderLayout());
        this.setTitle("NOTWA - NOT Only Team Work Assistent - User Management");
        this.setSize(500,300);
        this.setModalityType(JDialog.ModalityType.APPLICATION_MODAL);
        this.setLocationRelativeTo(null);
        
        this.add(this.initComponents(), BorderLayout.CENTER);
        this.add(this.initButtons(), BorderLayout.PAGE_END);
        
        this.setVisible(true);
    }
    
    private JComponent initComponents() {
        JPanel managementPanel = new JPanel(new GridLayout(1,0));
        
        pd = new ProjectDal(ci, context);
        pc = new ProjectCollection(context);
        pd.fill(pc);
        
        tblModel = new ProjectManagementModel(pc);
        table = new JTable(tblModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setCellRenderer(tableCellRenderer);
        table.getSelectionModel().addListSelectionListener(this);
        managementPanel.add(new JScrollPane(table));

        return managementPanel;
    }
    
    private JPanel initButtons() {
        JPanel jp = new JPanel();
        
        addButton = new JButton("Add");
        addButton.addActionListener(this);
        
        assignmentMngr = new JButton("Assignment manager");
        assignmentMngr.addActionListener(this);
        assignmentMngr.setEnabled(false);
        
        closeButton = new JButton("Close");
        closeButton.addActionListener(this);
        
        jp.add(addButton);
        jp.add(assignmentMngr);
        jp.add(closeButton);
        
        return jp;
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == closeButton) {
            this.setVisible(false);
        }
        
        if (ae.getSource() == addButton) {
            String msg = (String)JOptionPane.showInputDialog(this, "New project name", "NOTWA - New project creation", JOptionPane.QUESTION_MESSAGE);
            
            if (!msg.isEmpty()) {
                Project project = new Project();
                project.registerWithContext(context);
                project.setProjectName(msg);
                project.setInserted(true);
                
                pc.add(project);
                pd.update(pc);
                
                tblModel.fireTableDataChanged();
            }
            else {
                JOptionPane.showMessageDialog(this, "You must enter the name!", "NOTWA - Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        if (ae.getSource() == assignmentMngr) {
            AssignmentManager am = new AssignmentManager(ci, context);
            am.setSelection(this.getSelectedProject());
        }
    }

    private Project getSelectedProject() {
        int selectedIndex = table.convertRowIndexToModel(table.getSelectedRow());
        return pc.get(selectedIndex);
    }
    
    @Override
    public void valueChanged(ListSelectionEvent lse) {
        if (table.getSelectedRow() != -1)
            assignmentMngr.setEnabled(true);
        else
            assignmentMngr.setEnabled(false);
    }
    
    /*if(ae.getSource() == chooseColorButton) {
        JColorChooser colorChooser = new JColorChooser();
        JDialog jd = JColorChooser.createDialog( chooseColorButton,
                                    "Project color chooser",
                                    true,
                                    colorChooser,
                                    this,
                                    null);
        jd.setVisible(true); // not done yet 
    }*/
}
