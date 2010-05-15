/*
 * SettingsDialog
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
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import notwa.common.ApplicationSettings;
import notwa.common.Config;
import notwa.logger.LoggingFacade;

public class SettingsDialog extends JDialog implements ActionListener {

    private JButton okButton;
    private JButton stornoButton;
    private JComboBox cbSkin;
    private LookAndFeelInfo[] installedLAF = UIManager.getInstalledLookAndFeels();

    public SettingsDialog() {
        init();
    }
    
    public void init() {
        this.setLayout(new BorderLayout());
        this.setTitle("NOTWA - NOT Only Team Work Assistent - Settings");
        this.setSize(750,300);
        this.setModalityType(JDialog.ModalityType.APPLICATION_MODAL);
        this.setLocationRelativeTo(null);
        
        this.add(initComponenets(), BorderLayout.CENTER);
        this.add(initButtons(), BorderLayout.PAGE_END);
        
        this.setVisible(true);
    }
    
    private JPanel initComponenets() {
        JPanel jp = new JPanel();
        
        JLabel lSkin = new JLabel("Skin");
        cbSkin = getInstalledSkins();
        this.selectCurrentSkin();
        jp.add(lSkin);
        jp.add(cbSkin);
        
        return jp;
    }

    private JPanel initButtons() {
        JPanel jp = new JPanel();
        
        okButton = new JButton("Ok");
        okButton.addActionListener(this);
        
        stornoButton = new JButton("Storno");
        stornoButton.addActionListener(this);
        
        jp.add(okButton);
        jp.add(stornoButton);
        
        return jp;
    }

    private JComboBox getInstalledSkins() {
        JComboBox cbInstalledSkins = new JComboBox();
        
        for (int i = 0; i < installedLAF.length; i++) {
            cbInstalledSkins.addItem(installedLAF[i].getName());
        }
        
        return cbInstalledSkins;
    }
    
    private void selectCurrentSkin() {
        ApplicationSettings as = Config.getInstance().getApplicationSettings();
        String configuredSkin = as.getSkin();

        if ((configuredSkin == null) || configuredSkin.equals("")) {
            configuredSkin = UIManager.getLookAndFeel().getName();
            as.setSkin(configuredSkin);
            Config.getInstance().setApplicationsSettings(as);
            Config.getInstance().save();
        }

        cbSkin.setSelectedItem(configuredSkin);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == okButton) {
            //TODO: Config save
            ApplicationSettings as = new ApplicationSettings();
            as.setSkin(installedLAF[cbSkin.getSelectedIndex()].getClassName());
            System.out.println(installedLAF[cbSkin.getSelectedIndex()].getClassName());
            try {
                UIManager.setLookAndFeel(as.getSkin());
            }
            catch (Exception e) {
                LoggingFacade.handleException(e);
            }

            Config.getInstance().setApplicationsSettings(as);
            Config.getInstance().save();
            this.setVisible(false);
        }
        
        if (ae.getSource() == stornoButton) {
            this.setVisible(false);
        }
    }
}
