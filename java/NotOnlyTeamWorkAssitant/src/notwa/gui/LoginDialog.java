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
import java.awt.event.KeyEvent;
import java.util.Collection;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import notwa.common.ApplicationSettings;
import notwa.common.Config;
import notwa.common.ConnectionInfo;
import notwa.logger.LoggingFacade;
import notwa.exception.SignInException;
import notwa.gui.components.KeyValueComboBox;
import notwa.gui.components.NotwaProgressBar;
import notwa.security.Credentials;
import notwa.security.Security;
import notwa.threading.Action;
import notwa.threading.IndeterminateProgressThread;


/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class LoginDialog extends JDialog implements ActionListener {
    private JButton okButton, stornoButton;
    private KeyValueComboBox<ConnectionInfo> connectionsBox;
    private JTextField login;
    private JPasswordField password;
    private JLabel errorField = new JLabel(" ");
    private NotwaProgressBar progressBar = new NotwaProgressBar();
    private SignInParams signInParams = new SignInParams(null, null);
    private JCheckBox rememberUser;
    private ApplicationSettings appSettings = Config.getInstance().getApplicationSettings();
    
    public LoginDialog() {
        init();
    }
    
    public void init() {
        this.setLayout(new BorderLayout());
        this.setTitle("NOTWA - NOT Only Team Work Assistent - Sign-in");
        this.setSize(500,250);
        this.setModalityType(JDialog.ModalityType.APPLICATION_MODAL);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
                
        this.add(this.initComponents(), BorderLayout.CENTER);
        this.add(this.initButtons(), BorderLayout.SOUTH);
        this.add(progressBar, BorderLayout.NORTH);
        
        this.registerKeyListener();
        this.getInformationFromConfig();
        this.fillLoginName();

        this.setVisible(true);
    }
    
    private void registerKeyListener() {
        AbstractAction cancelAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                LoginDialog.this.setVisible(false); 
            }
        };

        AbstractAction okAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                 LoginDialog.this.actionPerformed(e);
            }
        };
        InputMap inputMap = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put( KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),  "CANCEL");
        inputMap.put( KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),  "OK");
        
        ActionMap actionMap = this.getRootPane().getActionMap();
        actionMap.put("CANCEL", cancelAction);
        actionMap.put("OK", okAction);
    }
    
    private void getInformationFromConfig() {
        rememberUser.setSelected(appSettings.isRememberNotwaLogin());
    }
    
    private JPanel initComponents() {
        JPanel componentsPanel = new JPanel();
        componentsPanel.setLayout(null);
        
        login = new JTextField();
        password = new JPasswordField();
        rememberUser = new JCheckBox("Remember login name");

        JLabel lDatabase = new JLabel("Database"); 
        lDatabase.setBounds(90, 19, 66, 15);
        componentsPanel.add(lDatabase);
        componentsPanel.add(initComboBox());
        connectionsBox.addActionListener(this);
        
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

        componentsPanel.add(rememberUser);
        rememberUser.setBounds(243, 105, 192, 20);
        
        componentsPanel.add(errorField);
        errorField.setBounds(156, 134, 192, 28);
        
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

    private KeyValueComboBox<ConnectionInfo> initComboBox() {
        connectionsBox = new KeyValueComboBox<ConnectionInfo>();
        connectionsBox.setEditable(false);
        connectionsBox.setBounds(243, 15, 150, 22);

        Collection<ConnectionInfo> cci = Config.getInstance().getConnecionStrings();
        for (ConnectionInfo connInfo : cci)
        {
            connectionsBox.addItem(connInfo,connInfo.getLabel());
        }
        
        return connectionsBox;
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if ((ae.getSource() == okButton) || (ae.getSource() == this.getRootPane())) {
            if (validateInput()) {
                initErrorField("You must fill all fields");
            } else {
                this.performSignIn();
            }
        } else if (ae.getSource() == stornoButton) {
            this.setVisible(false);
        }
        else if (ae.getSource() == connectionsBox) {
            this.fillLoginName();
            password.setText("");
        }

    }

    private boolean validateInput() {
        return (this.connectionsBox.getSelectedItem() == null ||
                this.login.getText().isEmpty() ||
                this.password.getPassword().length == 0);
    }

    private void performSignIn() {
        signInParams.connectionInfo = this.connectionsBox.getSelectedKey();
        signInParams.credentials = new Credentials(this.login.getText(), new String(this.password.getPassword()));
        final JDialog loginDialog = this;

        IndeterminateProgressThread ipt = new IndeterminateProgressThread(new Action<LoginDialog>(this) {
            @Override
            public void perform() {
                try {
                    params.stornoButton.setEnabled(false);
                    params.okButton.setEnabled(false);
                    Security.getInstance().signIn(signInParams.connectionInfo, signInParams.credentials);
                    
                    appSettings.setRememberNotwaLogin(rememberUser.isSelected());
                    Config.getInstance().setApplicationsSettings(appSettings);
                    if (rememberUser.isSelected()) {
                        signInParams.connectionInfo.getNotwaConnectionInfo().setNotwaUserName(login.getText());
                        Config.getInstance().setConnectionInfo(signInParams.connectionInfo);
                    }
                    Config.getInstance().save();
                    
                    params.setVisible(false);
                } catch (SignInException siex) {
                    JOptionPane.showMessageDialog(loginDialog, "Bad user name or password!");
                    LoggingFacade.handleException(siex);
                } finally {
                    params.stornoButton.setEnabled(true);
                    params.okButton.setEnabled(true);
                }
            }
        }, progressBar);

        ipt.run();
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

    public SignInParams getSignInParams() {
        return signInParams;
    }

    public class SignInParams {
        public ConnectionInfo connectionInfo;
        public Credentials credentials;

        public SignInParams(ConnectionInfo ci, Credentials c) {
            this.connectionInfo = ci;
            this.credentials = c;
        }
    }
    
    private void fillLoginName() {
        if (rememberUser.isSelected()) {
            login.setText(connectionsBox.getSelectedKey().getNotwaConnectionInfo().getNotwaUserName());
        }
        else {
            login.setText("");
        }
    }
}
