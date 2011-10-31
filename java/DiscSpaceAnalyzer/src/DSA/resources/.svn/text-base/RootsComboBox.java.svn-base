/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package DSA.resources;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;


/**
 *
 * @author tomas
 */
public class RootsComboBox extends JComboBox {

    public RootsComboBox(File[] rootsList) {
        super(new String[]{"Choose ..."});
        this.setRenderer(new ComboBoxRenderer());
        this.appendAllElements(rootsList);
    }

    private void appendAllElements(File[] listRoots) {
        for (File root : listRoots) {
            this.addItem(root);
        }
    }
    
    @Override
    public boolean isFocusable() {
        return false;
    }

    @Override
    public Font getFont() {
        return new Font("Arial", Font.BOLD, 13);
    }

    @Override
    public Border getBorder() {
        return BorderFactory.createLineBorder(Color.BLACK);
    }
}



class ComboBoxRenderer extends JLabel implements ListCellRenderer {
    protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

    public ComboBoxRenderer() {
        this.setOpaque(true);
    }

    public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        
        try {
            File root = ((File)value);
            renderer.setText("<html><b>" + root.getPath() + "</b> &nbsp;<font size=\"2\"><i>(" + convertToGigaBytes(root.getTotalSpace()) + "Gb)</i></font></html>");
        }
        catch (Exception e) {
            renderer.setText("<html>&nbsp;<font size=\"2\"><i>Choose ...</font></html>");
        }

        return renderer;
    }
    
    private double convertToGigaBytes(long number) {
            double numberForConvertion = number / (1024d * 1024d * 1024d);
            DecimalFormatSymbols dfs = new DecimalFormatSymbols();
            dfs.setDecimalSeparator('.');
            DecimalFormat df = new DecimalFormat("00.00", dfs);
            String formatedNumber = df.format(numberForConvertion);
            double convertedNumber = Double.parseDouble(formatedNumber);

            return convertedNumber;
    }
}