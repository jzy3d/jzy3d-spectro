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

import java.awt.Color;
import java.awt.Graphics;
import java.net.URL;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JRadioButton;

/**
 * A button for choosing a tool. The tool selection is represented by an icon
 * which is darkened when selected.
 *
 * @see Tool
 */
public class ToolButton extends JRadioButton {

    private final Tool tool;

    public ToolButton(Tool tool, String iconName, ButtonGroup group) {
        super(loadIcon(iconName));
        this.tool = tool;
        group.add(this);
    }
    
    private static Icon loadIcon(String name) {
        URL resourceUrl = ToolboxPanel.class.getResource("/icons/" + name + ".png");
        if (resourceUrl == null) {
            throw new RuntimeException("Missing icon resource: " + name);
        }
        return new ImageIcon(resourceUrl);
    }

    public Tool getTool() {
        return tool;
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (isSelected()) {
            g.setColor(Color.RED);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
        getIcon().paintIcon(
                this, g,
                getWidth() / 2 - getIcon().getIconWidth() / 2,
                getHeight() / 2 - getIcon().getIconHeight() / 2);
    }
}
