/*
 * UserEditor
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
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import notwa.gui.components.JTextFieldLimit;
import notwa.wom.User;

/**
 * User editor is used to create new or edit existing user.
 * 
 * @author Tomas Studnicka
 */
public class UserEditor extends JDialog implements ActionListener{
    private JButton okButton, stornoButton;
    private JTextField login,firstName,lastName;
    private JPasswordField password,secondPassword;
    private User user;
    private boolean newUser;
    
    public UserEditor(User user, boolean newUser) {
        this.user = user;
        this.newUser = newUser;
    }
    
    public void init() {
        this.setLayout(new BorderLayout());
        this.setTitle("NOTWA - NOT Only Team Work Assistent - User Editor");
        this.setSize(500,300);
        this.setModalityType(JDialog.ModalityType.APPLICATION_MODAL);
        this.setLocationRelativeTo(null);
        
        this.add(this.initMainComponents(),BorderLayout.CENTER);
        this.add(this.initButtons(), BorderLayout.PAGE_END);

        this.fillUserProperties();
        this.setVisible(true);
    }
    
    private void fillUserProperties() {
        if (this.user != null && !newUser) {
            this.login.setEnabled(false);
            this.login.setText(this.user.getLogin());
            this.password.setText("|_PASSWORD_PROTECTED_|");
            this.secondPassword.setText("|_PASSWORD_PROTECTED_|");
            this.firstName.setText(this.user.getFirstName());
            this.lastName.setText(this.user.getLastName());
        }
    }
    
    private JPanel initMainComponents() {
        JPanel componentsPanel = new JPanel();
        componentsPanel.setLayout(null);

        JLabel lLogin = new JLabel("Login");
        componentsPanel.add(lLogin);
        lLogin.setBounds(65, 33, 35, 15);
        login = new JTextField();
        componentsPanel.add(login);
        login.setBounds(211, 30, 112, 22);
        login.setDocument(new JTextFieldLimit(20));

        JLabel lPassword = new JLabel("Password");
        componentsPanel.add(lPassword);
        lPassword.setBounds(65, 61, 60, 15);
        password = new JPasswordField();
        componentsPanel.add(password);
        password.setBounds(211, 58, 157, 22);
        password.setDocument(new JTextFieldLimit(30));

        JLabel lPasswordVerification = new JLabel("Password verification");
        componentsPanel.add(lPasswordVerification);
        lPasswordVerification.setBounds(65, 89, 144, 15);
        secondPassword = new JPasswordField();
        componentsPanel.add(secondPassword);
        secondPassword.setBounds(211, 86, 157, 22);
        secondPassword.setDocument(new JTextFieldLimit(30));
        
        JLabel lFirstName = new JLabel("First name");
        componentsPanel.add(lFirstName);
        lFirstName.setBounds(65, 117, 70, 15);
        firstName = new JTextField();
        componentsPanel.add(firstName);
        firstName.setBounds(211, 114, 157, 22);
        firstName.setDocument(new JTextFieldLimit(30));

        JLabel lLastName = new JLabel("Last name");
        componentsPanel.add(lLastName);
        lLastName.setBounds(65, 145, 65, 15);
        lastName = new JTextField();
        componentsPanel.add(lastName);
        lastName.setBounds(211, 142, 157, 22);
        lastName.setDocument(new JTextFieldLimit(30));

        return componentsPanel;
    }
    
    private JPanel initButtons() {
        JPanel buttonsPanel = new JPanel();
        
        okButton = new JButton("Ok");
        stornoButton = new JButton("Storno");
        
        okButton.addActionListener(this);
        stornoButton.addActionListener(this);
        
        buttonsPanel.add(okButton);
        buttonsPanel.add(stornoButton);
        
        return buttonsPanel;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == okButton) {
            if (!this.login.getText().isEmpty() &&
                 new String(this.password.getPassword()).equals(new String(this.secondPassword.getPassword())) &&
                 this.password.getPassword().length != 0) {
                user.setLogin(this.login.getText());
                user.setFirstName(this.firstName.getText());
                user.setLastName(this.lastName.getText());
                if (!this.password.getPassword().toString().equals("|_PASSWORD_PROTECTED_|")) {
                    user.setPassword(new String(this.password.getPassword()));
                }
                
                user.setInserted(newUser);
                this.setVisible(false);
            } else {
                JOptionPane.showMessageDialog(this, "Recheck your login and password!");
            }
        }
        
        if (ae.getSource() == stornoButton) {
            user.setInserted(false);
            this.setVisible(false);
        }
    }
}