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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;

import notwa.common.ConnectionInfo;
import notwa.common.EventHandler;
import notwa.wom.Context;
import notwa.wom.WorkItem;

public class WorkItemDetailLayout extends JComponent implements ActionListener {
    private JTabbedPane detailTabs;
    private JButton hideDetail;
    protected WorkItemDetail wid;
    protected WorkItemNoteHistoryTable winht;
    private EventHandler<GuiEvent> guiHandler;
    protected Context context;
    protected ConnectionInfo ci;

    public WorkItemDetailLayout() {
        init();
    }
    
    public void init() {

        /**
         * Instantiate all GUI components
         */
        detailTabs = new JTabbedPane();
        hideDetail = new JButton("Hide detail");
        wid = new WorkItemDetail();
        winht = new WorkItemNoteHistoryTable();

        /**
         * Setup the hide detail button
         */
        hideDetail.addActionListener(this);

        /**
         * Setup the tabs
         */
        detailTabs.addTab("Detail", wid);
        detailTabs.addTab("Notes history", winht);

        /**
         * Setup this component
         */
        this.setLayout(new BorderLayout());
        this.add(hideDetail, BorderLayout.PAGE_START);
        this.add(detailTabs, BorderLayout.CENTER);
    }

    public void onFireGuiEvent(EventHandler<GuiEvent> guiHandler) {
        this.guiHandler = guiHandler;
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        guiHandler.handleEvent(new GuiEvent(new GuiEventParams(GuiEventParams.ACTION_EVENT_HIDE_DETAIL)));
    }

    public void setDataToNull() {
        wid.setAllToNull();
        winht.setAllToNull();
    }

    public void onSelectedWorkItemChanged(WorkItem wi, ConnectionInfo connectionInfo, Context context) {
        this.context = context;
        this.ci = connectionInfo;
        wid.loadFromWorkItem(wi);
        winht.loadFromWorkItem(wi);
        wid.setWorkItemNoteHistoryTable(winht);
    }
}
