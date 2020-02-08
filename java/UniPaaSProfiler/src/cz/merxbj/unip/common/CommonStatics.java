package cz.merxbj.unip.common;

import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author mrneo
 */
public class CommonStatics {
    public static final Color DARK_GREEN = new Color(0, 150, 0);
    public static final Color GREEN = new Color(130,255,60);
    public static final Color LIGHT_GREEN = new Color(210,255,160);
    public static final Color LIGHT_BLUE = new Color(170, 220, 255);
    public static final Color DEFAULT_BACKGROUND = new Color(255,255,255);
    public static final Color ERROR = new Color(255,140,140);
    
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormat.forPattern("HH:mm:ss.SSS");
    
    private CommonStatics () {
    }
    
    public static void invokeErrorDialog(String text) {
        JOptionPane.showMessageDialog(new JFrame(), text, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
