/*
 * Created on Aug 26, 2008
 *
 * Spectro-Edit is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * Spectro-Edit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 */
package net.bluecow.spectro.tool;

import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;

import javax.swing.border.Border;

/**
 * A border that only takes up space on the top of a component, and
 * simply renders the title string followed by a horizontal line long
 * enough to fill out the component's current width. 
 */
public class TitleBorder implements Border {

    private String title;

    public TitleBorder(String title) {
        this.title = title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getTitle() {
        return title;
    }
    
    public Insets getBorderInsets(Component c) {
        int height = c.getFontMetrics(getFont(c)).getHeight();
        return new Insets(height, 0, 0, 0);
    }

    public boolean isBorderOpaque() {
        return false;
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g;
        Font font = getFont(c);
        FontMetrics fm = c.getFontMetrics(font);
        g.setFont(font);
        g2.drawString(title, 0, fm.getAscent());
        g2.drawLine(fm.stringWidth(title) + 5, fm.getHeight()/2, width, fm.getHeight()/2);
    }

    private Font getFont(Component c) {
        return c.getFont().deriveFont(Font.BOLD);
    }
}
