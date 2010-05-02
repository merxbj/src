/*
 * JStatusBar
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
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import notwa.gui.components.NotwaProgressBar;
import notwa.threading.IndeterminablePrgoressExpressioner;

public class JStatusBar extends JPanel implements IndeterminablePrgoressExpressioner {
    NotwaProgressBar mainProgressBar = new NotwaProgressBar();
    JLabel statusBarText = new JLabel();
    private final String idleMessage = "Welcome to Not Only Team Work Assistant!";
    
    public JStatusBar() {
        setStatusBarText(idleMessage);
        setProgressBarValue(0);
        
        this.setLayout(new BorderLayout());
        this.add(statusBarText, BorderLayout.LINE_START);
        mainProgressBar.setPreferredSize(new Dimension(300,20));
        mainProgressBar.setStringPainted(true);
        this.add(mainProgressBar, BorderLayout.LINE_END);
    }
    
    public void setStatusBarText(String text) {
        this.statusBarText.setText(text);
        this.update(this.getGraphics());
    }
    
    public void setProgressBarValue(int v) {
        mainProgressBar.setValue(v);
        mainProgressBar.updateUI();
    }
    
    public void setProgressBarMaximum(int v) {
        mainProgressBar.setMaximum(v);
        mainProgressBar.updateUI();
    }

    @Override
    public void beginIndetermination() {
        statusBarText.setText("Synchronizing with repository ...");
        mainProgressBar.setValue(mainProgressBar.getMinimum());
        mainProgressBar.beginIndetermination();
    }

    @Override
    public void endIndetermination() {
        mainProgressBar.endIndetermination();
        statusBarText.setText(idleMessage);
    }
}
