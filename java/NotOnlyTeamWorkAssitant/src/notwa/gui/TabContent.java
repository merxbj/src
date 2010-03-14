package notwa.gui;

import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JSplitPane;

import notwa.wom.WorkItemCollection;

@SuppressWarnings("serial")
public class TabContent extends JComponent {
    static JSplitPane sp;
    private static boolean hideDetail;
    //TODO: both must have parameter to know what information we want to show
    public TabContent() {
    }
    
    public TabContent initTabContent(WorkItemCollection wic) {
        this.setLayout(new GridLayout(1,0));
        
        WorkItemTableLayout witl = new WorkItemTableLayout(wic);
        
        WorkItemDetail wid = new WorkItemDetail();
        
        sp = new JSplitPane(    JSplitPane.VERTICAL_SPLIT,
                                        witl, wid);
        sp.setResizeWeight(0.7);
        sp.setContinuousLayout(true);
        this.add(sp);
        return this;
    }
    
    public static void ShowHideDetail() {
        if (hideDetail) {
            sp.setDividerLocation(-1);
            hideDetail = false;
        }
        else {

            //TODO after fullscreen, is detail visible anyway ?!
            sp.setDividerLocation(50000);
            hideDetail = true;
        }
    }
}
