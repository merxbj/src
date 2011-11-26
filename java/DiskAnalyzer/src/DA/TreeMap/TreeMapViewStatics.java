/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DA.TreeMap;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

/**
 *
 * @author mrneo
 */
public class TreeMapViewStatics {

    public static final Font DEFAULT_VIEW_FONT = new Font("Arial", Font.PLAIN, 10);
    public static final int DEFAULT_CONTENT_OFFSET = 10;

    public static Color getDepthColor(int depth, boolean isFile) {
        int currentDepth = (depth == 0) ? 1 : depth + 1;
        if (!isFile) {
            int red = ((100 - (10 * currentDepth)) > 0) ? 100 - (10 * currentDepth) : 0;
            int green = ((225 - (10 * currentDepth)) > 0) ? 225 - (10 * currentDepth) : 0;
            int blue = 255 - (10 * currentDepth);

            return new Color(red, green, blue);
        } else {
            int red = ((100 - (10 * currentDepth)) > 0) ? 100 - (10 * currentDepth) : 0;
            int green = 255 - (10 * currentDepth);
            int blue = ((150 - (10 * currentDepth)) > 0) ? 150 - (10 * currentDepth) : 0;

            return new Color(red, green, blue);
        }
    }

    public static void drawString(Graphics g, String text, int x, int y, int width, int height) {
        FontMetrics fm = g.getFontMetrics();

        if (fm.getHeight() > height) {
            return;
        } else if (fm.stringWidth(text) <= width) {
            g.drawString(text, x, y);
            return;
        } else {
            int currentX = x;
            char[] charArray = text.toCharArray();
            for (char singleChar : charArray) {
                int charWidth = fm.charWidth(singleChar);

                if ((currentX + charWidth) >= (x + width)) {
                    return;
                }

                g.drawString(String.valueOf(singleChar), currentX, y);

                currentX += charWidth;
            }
        }
    }
}
