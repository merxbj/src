package notwa.gui;

import java.awt.GridLayout;

import javax.swing.JLabel;

@SuppressWarnings("serial")
public class WorkItemDetail extends MainLayoutLoader {
    public WorkItemDetail() {
        this.setLayout(new GridLayout(3,0));
        
        this.add(new JLabel("aditional informations"));
        this.add(new JLabel("more informations"));
        this.add(new JLabel("mooooore informations"));
    }
}
