/*
 * LoginDialog
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
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import notwa.common.Config;
import notwa.common.ConnectionInfo;
import notwa.common.LoggingInterface;
import notwa.exception.SignInException;
import notwa.security.Credentials;
import notwa.security.Security;

public class LoginDialog extends JDialog implements ActionListener {
    private JButton okButton, stornoButton;
    private JComboBox jcb;
    private JTextField login;
    private JPasswordField password;
    private JLabel errorField = new JLabel(" ");
    
    public LoginDialog() {
    }
    
    public void initLoginDialog() {
        this.setLayout(new BorderLayout());
        this.setTitle("NOTWA - NOT Only Team Work Assistent - Sign-in");
        this.setSize(500,200);
        this.setModalityType(JDialog.ModalityType.APPLICATION_MODAL);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
                
        this.add(this.initComponents(), BorderLayout.CENTER);

        this.add(this.initButtons(), BorderLayout.PAGE_END);
        
        this.setVisible(true);
    }
    
    private JPanel initComponents() {
        JPanel componentsPanel = new JPanel();
        componentsPanel.setLayout(null);
        
        login = new JTextField();
        password = new JPasswordField();

        JLabel lDatabase = new JLabel("Database"); 
        lDatabase.setBounds(90, 19, 66, 15);
        componentsPanel.add(lDatabase);
        componentsPanel.add(initComboBox());
        
        JLabel lLogin = new JLabel("Login");
        lLogin.setBounds(90, 48, 66, 15);
        componentsPanel.add(lLogin);
        componentsPanel.add(login);
        login.setBounds(243, 46, 150, 20);
        
        JLabel lPassword = new JLabel("Password");
        lPassword.setBounds(90, 77, 66, 15);
        componentsPanel.add(lPassword);
        componentsPanel.add(password);
        password.setBounds(243, 75, 150, 20);

        componentsPanel.add(errorField);
        errorField.setBounds(156, 107, 192, 20);
        
        return componentsPanel;
    }
    
    private JPanel initButtons() {
        JPanel jp = new JPanel();
        
        okButton = new JButton("Ok");
        stornoButton = new JButton("Storno");
        
        okButton.addActionListener(this);
        stornoButton.addActionListener(this);
        
        jp.add(okButton);
        jp.add(stornoButton);
        
        return jp;
    }

    private JComboBox initComboBox() {
        jcb = new JComboBox();
        jcb.setEditable(false);
        jcb.setBounds(243, 15, 150, 22);

        Collection<ConnectionInfo> cci = Config.getInstance().getConnecionStrings();
        for (ConnectionInfo connInfo : cci)
        {
            jcb.addItem(new JComboBoxItemCreator(connInfo,connInfo.getLabel()));
        }
        
        return jcb;
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == okButton) {
            /*
             * TODO del
             */
            login.setText("mrneo");
            password.setText("aaaa");
            /*
             * 
             */
            if (    this.jcb.getSelectedItem().equals("") ||
                    this.login.getText().isEmpty() ||
                    this.password.getPassword().length == 0) {
                initErrorField("You must fill all fields");
            }
            else {
                this.performSignIn();
                this.setVisible(false);
            }
        }
        if (ae.getSource() == stornoButton) {
            this.setVisible(false);
        }
    }

    private void performSignIn() {
        try {
            ConnectionInfo ci = (ConnectionInfo)((JComboBoxItemCreator)
                                this.jcb.getSelectedItem()).getAttachedObject();
            Credentials credentials = new Credentials(this.login.getText(), new String(this.password.getPassword()));
            Security.getInstance().signIn(ci, credentials);
            MainWindow.getTabController().createWitView(ci, credentials);
        } catch (SignInException siex) {
            LoggingInterface.getInstanece().handleException(siex);
        } catch (Exception ex) {
            LoggingInterface.getInstanece().handleException(ex);
        }
    }

    private void initErrorField(String errorMessage) {
        Font boldedFont = new Font( this.errorField.getFont().getFamily(),
                                    Font.BOLD,
                                    15);
        
        this.errorField.setText(errorMessage);
        this.errorField.setForeground(new Color(255,0,0));
        this.errorField.setFont(boldedFont);
        this.errorField.updateUI();
    }
}
