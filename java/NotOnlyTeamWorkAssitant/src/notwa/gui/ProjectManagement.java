package notwa.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import notwa.common.ConnectionInfo;
import notwa.dal.ProjectDal;
import notwa.dal.ProjectToUserAssignmentDal;
import notwa.exception.ContextException;
import notwa.gui.components.JAnyItemCreator;
import notwa.logger.LoggingFacade;
import notwa.wom.Context;
import notwa.wom.Project;
import notwa.wom.ProjectCollection;
import notwa.wom.User;

public class ProjectManagement extends JDialog implements ActionListener {
    private ConnectionInfo ci;
    private Context context;
    private JButton closeButton;

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
    
    private JPanel initComponents() {
        JPanel jp = new JPanel();
        
        return jp;
    }
    
    private JPanel initButtons() {
        JPanel jp = new JPanel();
        
        closeButton = new JButton("Close");
        closeButton.addActionListener(this);
        
        return jp;
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == closeButton) {
            this.setVisible(false);
        }
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
    /*            else {
                Project project = new Project();
                project.registerWithContext(context);
                project.setProjectName(newProjectName.getText());
                try {
                    User user = (User)((JAnyItemCreator)users.getSelectedItem()).getAttachedObject();
                    user.setInserted(true);
                    
                    project.addAssignedUser(user);
                } catch (ContextException ex) {
                    JOptionPane.showMessageDialog(this, "New project creation has failed, check log for further information");
                    LoggingFacade.handleException(ex);
                    close = false;
                }
                
                ProjectCollection pc = new ProjectCollection(context);
                
                ProjectDal pd = new ProjectDal(ci, context);
                pd.fill(pc);
                pc.add(project);
                pd.update(pc);
                
                ProjectToUserAssignmentDal ptuad = new ProjectToUserAssignmentDal(ci, context);
                ptuad.update(project.getAssignedUsers());
                wi.setProject(project);
            }*/
}
