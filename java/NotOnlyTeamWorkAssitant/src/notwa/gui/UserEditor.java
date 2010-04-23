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
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import notwa.common.ConnectionInfo;
import notwa.dal.UserDal;
import notwa.exception.ContextException;
import notwa.wom.Context;
import notwa.wom.ContextManager;
import notwa.wom.User;
import notwa.wom.UserCollection;

/**
 * User editor is used to create new or edit existing user.
 * 
 * @author mrneo
 */
public class UserEditor extends JDialog implements ActionListener{
    private JButton okButton, stornoButton;
    private JTextField login,firstName,lastName;
    private JPasswordField password,secondPassword;
    private String originalUserPassword;
    private User user;
    
    public UserEditor(User user) {
        this.user = user;
        
        if (user != null) {
            this.login.setText(user.getLogin());
            this.password.setText("");
            this.secondPassword.setText("");
            this.firstName.setText(user.getFirstName());
            this.lastName.setText(user.getLastName());
            this.originalUserPassword = user.getPassword();
        }
    }
    
    public void init() {
        this.setLayout(new BorderLayout());
        this.setTitle("NOTWA - NOT Only Team Work Assistent - User Editor");
        this.setSize(500,300);
        this.setModalityType(JDialog.ModalityType.APPLICATION_MODAL);
        this.setLocationRelativeTo(null);
        
        this.add(this.initMainComponents(),BorderLayout.CENTER);
        this.add(this.initButtons(), BorderLayout.PAGE_END);
        
        this.setVisible(true);
    }
    
    private JPanel initMainComponents() {
        JPanel componentsPanel = new JPanel();
        componentsPanel.setLayout(null);

        JLabel lLogin = new JLabel("Login");
        componentsPanel.add(lLogin);
        lLogin.setBounds(30, 18, 35, 15);
        login = new JTextField();
        componentsPanel.add(login);
        login.setBounds(174, 15, 112, 22);

        JLabel lPassword = new JLabel("Password");
        componentsPanel.add(lPassword);
        lPassword.setBounds(30, 46, 60, 15);
        password = new JPasswordField();
        componentsPanel.add(password);
        password.setBounds(174, 43, 112, 22);

        JLabel lPasswordVerification = new JLabel("Password verification");
        componentsPanel.add(lPasswordVerification);
        lPasswordVerification.setBounds(30, 73, 130, 15);
        secondPassword = new JPasswordField();
        componentsPanel.add(secondPassword);
        secondPassword.setBounds(174, 70, 112, 22);

        JLabel lFirstName = new JLabel("First name");
        componentsPanel.add(lFirstName);
        lFirstName.setBounds(30, 100, 70, 15);
        firstName = new JTextField();
        componentsPanel.add(firstName);
        firstName.setBounds(174, 97, 112, 22);

        JLabel lLastName = new JLabel("Last name");
        componentsPanel.add(lLastName);
        lLastName.setBounds(30, 127, 65, 15);
        lastName = new JTextField();
        componentsPanel.add(lastName);
        lastName.setBounds(174, 124, 112, 22);

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
            this.setVisible(false);
        }
        
        if (ae.getSource() == stornoButton) {
            this.setVisible(false);
        }
    }
}