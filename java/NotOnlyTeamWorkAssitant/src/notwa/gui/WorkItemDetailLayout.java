/*
 * WorkItemDetailLayout
 *
 * Copyright (C) 2010  Jaroslav Merxbauer
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package notwa.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import notwa.common.EventHandler;

public class WorkItemDetailLayout extends JComponent implements ActionListener {
    private static WorkItemDetailLayout instance;
    JTabbedPane detailTabs = new JTabbedPane();
    JButton hideDetail = new JButton("Hide detail");
    private WorkItemDetail wid;
    private WorkItemNoteHistoryTable winht;
    private EventHandler<GuiEvent> guiHandler;

    public WorkItemDetailLayout() {
    }
    
    public static WorkItemDetailLayout getInstance() {
        if (instance == null) {
            instance = new WorkItemDetailLayout();
        }
        return instance;
    }
    
    public Component initDetailLayout() {
        this.setLayout(new BorderLayout());
    
        this.add(hideDetail, BorderLayout.PAGE_START);
        hideDetail.addActionListener(this);
        
        detailTabs.addTab("Detail", WorkItemDetail.getInstance().initComponents());
        detailTabs.addTab("Notes history", WorkItemNoteHistoryTable.getInstance().initNoteHistoryTable());

        this.add(detailTabs, BorderLayout.CENTER);
            
        return this;
    }

    public void onFireGuiEvent(EventHandler<GuiEvent> guiHandler) {
        this.guiHandler = guiHandler;
    }

    public WorkItemDetail getWorkItemDetail() {
        return this.wid;
    }
    
    public WorkItemNoteHistoryTable getWorkItemNoteHistoryTable() {
        return this.winht;
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        guiHandler.handleEvent(new GuiEvent(new GuiEventParams(GuiEventParams.ACTION_EVENT_HIDE_DETAIL)));
    }
}
