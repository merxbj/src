package notwa.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import notwa.common.Config;
import notwa.common.ConnectionInfo;
import notwa.common.LoggingInterface;
import notwa.dal.UserDal;
import notwa.exception.SignInException;
import notwa.security.Security;
import notwa.sql.Parameters.User;

@SuppressWarnings("serial")
public class LoginDialog extends JFrame implements ActionListener {
    private JButton okButton, stornoButton;
    private JComboBox jcb;
    private JTextField login;
    private JPasswordField password;
    private JLabel errorField = new JLabel();
    
    public LoginDialog() {
    }
    
    public void initLoginDialog() {
        this.setLayout(new BorderLayout());
        this.setTitle("NOTWA - NOT Only Team Work Assistent - Sign-in");
        this.setSize(500,200);
        this.setLocationRelativeTo(null);

        this.add(this.errorField, BorderLayout.PAGE_START);
        
        this.add(this.initComponents(), BorderLayout.CENTER);

        this.add(this.initButtons(), BorderLayout.PAGE_END);
        
        this.setVisible(true);
    }
    
    private JPanel initComponents() {
        JPanel jp = new JPanel(new GridLayout(0,2));
        jcb = new JComboBox();
        login = new JTextField();
        password = new JPasswordField();

        jcb.setEditable(false);
        login.setPreferredSize(new Dimension(150,20));
        password.setPreferredSize(new Dimension(150,20));;
        /*
         * fill combobox with all existing db connections
         */
        //TODO make combobox to accept hasmap/table
        Collection<ConnectionInfo> cci = Config.getInstance().getConnecionStrings();
        for (ConnectionInfo connInfo : cci)
        {
            jcb.addItem(new JComboBoxItemCreator(connInfo,connInfo.getLabel()));
        }
        
        jp.add(new JLabel("Database"));
        jp.add(jcb);
        
        jp.add(new JLabel("Login"));
        jp.add(login);
        
        jp.add(new JLabel("Password"));
        jp.add(password);
        
        return jp;
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

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == okButton) {
           /* if (!   this.jcb.getSelectedItem().equals("") ||
                    this.login.getText().isEmpty() ||
                    this.password.getPassword().length != 0) {
                initErrorField("You must fill all fields");
            }
            else {  --- DISABLED FOR TESTING --- because now is everything null :D*/
                try {
                    ConnectionInfo ci = ((JComboBoxItemCreator)this.jcb.getSelectedItem()).getAttachedConnectionInfo();
                    if(Security.getInstance().signIn(  ci,
                                                    this.login.getText(),
                                                    new String(this.password.getPassword()))) {
                        MainWindow.getTabController().createWitView(ci);
                    }
                    else {
                        throw new Exception ("User login failed");
                    }
                } catch (SignInException siex) {
                    LoggingInterface.getInstanece().handleException(siex);
                } catch (Exception ex) {
                    LoggingInterface.getInstanece().handleException(ex);
                }
                
                this.setVisible(false);
            //}
        }
        if (ae.getSource() == stornoButton) {
            this.setVisible(false);
        }
    }

    private void initErrorField(String errorMessage) {
        Font boldedFont = new Font( this.errorField.getFont().getFamily(),
                                    Font.BOLD,
                                    this.errorField.getFont().getSize()+3);
        
        this.errorField.setText(errorMessage);
        this.errorField.setForeground(new Color(255,0,0));
        this.errorField.setFont(boldedFont);
        this.errorField.updateUI();
    }
}
