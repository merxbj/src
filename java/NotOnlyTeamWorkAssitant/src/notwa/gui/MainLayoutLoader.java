package notwa.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import notwa.common.ConnectionInfo;
import notwa.dal.WorkItemDal;
import notwa.wom.Note;
import notwa.wom.NoteCollection;
import notwa.wom.Project;
import notwa.wom.User;
import notwa.wom.WorkItem;
import notwa.wom.WorkItemCollection;

@SuppressWarnings("serial")
public class MainLayoutLoader extends JPanel implements ActionListener {
    private JTabbedPane tabPanel;
    private JButton plusButton;
    
    public MainLayoutLoader () {
        //TODO: must be loaded from config - lastly used tabs(databases) etc.
    }

    public Component initMainLayout() {
        this.setLayout(new GridLayout(1,0));
        this.add(loadTabs());
        return this;
    }
    
    public Component loadTabs() {
        this.setLayout(new BorderLayout());
        tabPanel = new JTabbedPane();

        TabContent tc = new TabContent();
        
        
        /*
         * TODO: delete !!!TESTING DATA!!!
         */
        WorkItemCollection wic = new WorkItemCollection();
        wic.add(fillWithSomeData());
        wic.add(fillWithSomeData());
        wic.add(fillWithSomeData());
        wic.add(fillWithSomeData());
        /*
         * 
         */
        
        tabPanel.addTab("Default", tc.initTabContent(wic));
        
        //create empty tab, where we will attach new button
        tabPanel.addTab(null,null); 

        tabPanel.setTabComponentAt(tabPanel.getTabCount() - 1, this.initPlusButton());

        return tabPanel;        
    }
    
    private JButton initPlusButton() {
        plusButton = new JButton("+");
        plusButton.setBorder(null);
        plusButton.setPreferredSize(new Dimension(30,20));
        plusButton.addActionListener(this);
        
        return plusButton;
    }

    public void createWitView(ConnectionInfo ci) {
        WorkItemCollection wic = new WorkItemCollection();
        WorkItemDal wid = new WorkItemDal(ci);
        wid.Fill(wic);
        
        TabContent tc = new TabContent();
        tabPanel.addTab(ci.getLabel(), tc.initTabContent(wic));
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == plusButton) {
            LoginDialog ld = new LoginDialog();
            ld.initLoginDialog();
        }
    }
    
    private WorkItem fillWithSomeData() {
        User user = new User(1);
        user.setLogin("mrneo");
        
        Project project = new Project(1);
        project.setProjectName("notwa");
        
        Note note = new Note(1, 1);
        note.setAuthor(user);
        note.setNoteText("kurva jedna zasrana");
        
        NoteCollection noteCollection = new NoteCollection();
        noteCollection.add(note);
        
        
        WorkItem wi = new WorkItem(1);
        wi.setSubject("class CloningTest");
        wi.setAssignedUser(user);
        wi.setDescription("create new class CloningTest for testing this code...");
        wi.setProject(project);
        wi.setNoteCollection(noteCollection);
        
        return wi;
    }
}
