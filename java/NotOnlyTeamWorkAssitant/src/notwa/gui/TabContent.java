package notwa.gui;

import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

@SuppressWarnings("serial")
public class TabContent extends JComponent {
    
    //TODO: both must have parameter to know what information we want to show
    public TabContent() {
        this.setLayout(new GridLayout(1,0));
        
        WorkItemTableLayout witl = new WorkItemTableLayout();
        
        WorkItemDetail wid = new WorkItemDetail();
        
        JSplitPane sp = new JSplitPane(    JSplitPane.VERTICAL_SPLIT,
                                        witl, wid);
        sp.setResizeWeight(0.7);
        sp.setContinuousLayout(true);
        this.add(sp);
    }
}
