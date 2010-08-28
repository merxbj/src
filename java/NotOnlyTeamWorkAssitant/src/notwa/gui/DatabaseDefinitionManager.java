/*
 * DatabaseDefinitionManager
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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellEditor;

import notwa.common.Config;
import notwa.common.NotwaConnectionInfo;
import notwa.gui.components.JPasswordFieldRenderer;
import notwa.gui.components.JTableCellRenderer;
import notwa.gui.datamodels.DatabaseManagerModel;

public class DatabaseDefinitionManager extends JDialog implements ActionListener {
    private JButton okButton;
    private JButton stornoButton;
    private JButton newRowButton;
    private JButton removeButton;
    private JTable ddmTable;
    private DatabaseManagerModel dmModel;
    private JTableCellRenderer tableCellRenderer = new JTableCellRenderer();
    private DefaultCellEditor passwordEditor;
    private Collection<NotwaConnectionInfo> nci;

    public DatabaseDefinitionManager() {
        init();
    }

    public void init() {
        this.setLayout(new BorderLayout());
        this.setTitle("NOTWA - NOT Only Team Work Assistent - Database definition");
        this.setSize(700,500);
        this.setModalityType(JDialog.ModalityType.APPLICATION_MODAL);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
 
        this.add(this.initComponents(), BorderLayout.NORTH);
        this.add(this.initButtons(), BorderLayout.SOUTH);
       
        this.setVisible(true);
    }

    private JPanel initButtons() {
        JPanel buttonsPanel = new JPanel();
        
        newRowButton = new JButton("Add row");
        removeButton = new JButton("Remove");
        okButton = new JButton("Ok");
        stornoButton = new JButton("Storno");
        
        newRowButton.addActionListener(this);
        removeButton.addActionListener(this);
        okButton.addActionListener(this);
        stornoButton.addActionListener(this);
        
        buttonsPanel.add(newRowButton);
        buttonsPanel.add(removeButton);
        buttonsPanel.add(okButton);
        buttonsPanel.add(stornoButton);
        
        return buttonsPanel;
    }

    private JPanel initComponents() {
        JPanel jp = new JPanel();
        jp.setLayout(new GridLayout(1,0));

        nci = Config.getInstance().getConnecionStrings();

        dmModel = new DatabaseManagerModel(nci);
        ddmTable = new JTable();
        ddmTable.setModel(dmModel);
        ddmTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        this.resizeAndColorizeTable();

        jp.add(new JScrollPane(ddmTable));
        
        return jp;
    }

    private void resizeAndColorizeTable() {
        /*
         * Default TextField that allows simple cell editing
         */
        JTextField defaultJTextField = new JTextField();
        defaultJTextField.setBorder(BorderFactory.createEmptyBorder());
        
        TableCellEditor textFieldEditor = new DefaultCellEditor(defaultJTextField);

        /*
         * Default PasswordField that allows password cell editing
         */
        JPasswordField defaultPasswordField = new JPasswordField();
        defaultPasswordField.setBorder(BorderFactory.createEmptyBorder());
        
        passwordEditor = new DefaultCellEditor(defaultPasswordField);
        /*
         * Setting up cell editors
         */
        ddmTable.getColumnModel().getColumn(0).setCellEditor(textFieldEditor);
        ddmTable.getColumnModel().getColumn(1).setCellEditor(textFieldEditor);
        ddmTable.getColumnModel().getColumn(2).setCellEditor(textFieldEditor);
        ddmTable.getColumnModel().getColumn(3).setCellEditor(textFieldEditor);
        ddmTable.getColumnModel().getColumn(4).setCellEditor(textFieldEditor);
        ddmTable.getColumnModel().getColumn(5).setCellEditor(passwordEditor);
        
        /*
         * Column resizing
         */
        ddmTable.getColumnModel().getColumn(0).setMaxWidth(180); //label
        ddmTable.getColumnModel().getColumn(1).setMaxWidth(110); //dbname
        ddmTable.getColumnModel().getColumn(2).setMaxWidth(110); //host
        ddmTable.getColumnModel().getColumn(3).setMaxWidth(50);  //port
        ddmTable.getColumnModel().getColumn(4).setMaxWidth(125); //user
        ddmTable.getColumnModel().getColumn(5).setMaxWidth(125); //password

        /*
         * Default cell renderers
         */
        ddmTable.getColumnModel().getColumn(0).setCellRenderer(tableCellRenderer);
        ddmTable.getColumnModel().getColumn(1).setCellRenderer(tableCellRenderer);
        ddmTable.getColumnModel().getColumn(2).setCellRenderer(tableCellRenderer);
        ddmTable.getColumnModel().getColumn(3).setCellRenderer(tableCellRenderer);
        ddmTable.getColumnModel().getColumn(4).setCellRenderer(tableCellRenderer);
        ddmTable.getColumnModel().getColumn(5).setCellRenderer(new JPasswordFieldRenderer());
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == okButton) {
            /*
             * We must stop editing before closing dialog and save all edited fields
             */
            try {
                ddmTable.getCellEditor().stopCellEditing();
            } catch (Exception e) {};

            dmModel.fireTableDataChanged();

            Config.getInstance().save();
            this.setVisible(false);

        }
        else if (ae.getSource() == stornoButton) {
            Config.getInstance().reloadConfig();
            this.setVisible(false);
        }
        else if (ae.getSource() == newRowButton) {
            dmModel.addRow(new Vector<Object>());
        }
        else if (ae.getSource() == removeButton) {
            if (ddmTable.getSelectedRow() != -1)
                dmModel.removeRow(ddmTable.getSelectedRow());
        }
    }
}