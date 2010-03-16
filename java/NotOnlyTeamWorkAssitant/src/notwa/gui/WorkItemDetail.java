package notwa.gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class WorkItemDetail extends JPanel implements ActionListener {
    JButton hideDetail = new JButton("Hide detail");

    public WorkItemDetail() {
        this.setLayout(new GridLayout(4,0));
        
        this.add(new JLabel("aditional informations"));
        this.add(new JLabel("more informations"));
        this.add(new JLabel("mooooore informations"));
        
        hideDetail.addActionListener(this);
        this.add(hideDetail);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == hideDetail) {
            TabContent.hideDetail();
        }
    }
}
