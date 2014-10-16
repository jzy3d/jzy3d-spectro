/*
 * Created on Aug 6, 2008
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

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;

import net.bluecow.spectro.Clip;
import net.bluecow.spectro.ClipPanel;
import net.bluecow.spectro.Frame;
import net.bluecow.spectro.SpectroEditSession;

/**
 * Tool that flips the data in the selected region horizontally and/or
 * vertically.
 */
public class RegionFlipTool implements Tool {

    private ClipPanel clipPanel;
    private Clip clip;
    
    private final Box settingsPanel;
    private final JButton vflipButton;
    private final JButton hflipButton;
    
    public RegionFlipTool() {
        settingsPanel = Box.createVerticalBox();

        vflipButton = new JButton("Flip vertically");
        vflipButton.setOpaque(false);
        settingsPanel.add(vflipButton);
        vflipButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                vflipRegion();
            }
        });
        
        hflipButton = new JButton("Flip horizontally");
        hflipButton.setOpaque(false);
        settingsPanel.add(hflipButton);
        hflipButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                hflipRegion();
            }
        });
        
        settingsPanel.add(Box.createGlue());
    }

    public String getName() {
        return "Flip";
    }
    
    public void activate(SpectroEditSession session) {
        this.clipPanel = session.getClipPanel();
        clip = clipPanel.getClip();
        clipPanel.setRegionMode(true);
    }

    public void deactivate() {
        clip = null;
        clipPanel = null;
    }

    public JComponent getSettingsPanel() {
        return settingsPanel;
    }

    private void vflipRegion() {
        Rectangle region = clipPanel.getRegion();
        if (region == null || region.width == 0 || region.height == 0) {
            return;
        }
        Rectangle frameRegion = clipPanel.toClipCoords(new Rectangle(region));
        clip.beginEdit(frameRegion, "Flip Region Vertically");
        for (int i = 0; i < frameRegion.width; i++) {
            Frame frame = clip.getFrame(frameRegion.x + i);
            for (int j = 0; j < frameRegion.height / 2; j++) {
                int bottom = frameRegion.y + frameRegion.height - 1 - j;
                int top = frameRegion.y + j;
                double tmp = frame.getReal(bottom);
                frame.setReal(bottom, frame.getReal(top));
                frame.setReal(top, tmp);
            }
        }
        clip.endEdit();
    }

    private void hflipRegion() {
        Rectangle region = clipPanel.getRegion();
        if (region == null || region.width == 0 || region.height == 0) {
            return;
        }
        Rectangle frameRegion = clipPanel.toClipCoords(new Rectangle(region));
        clip.beginEdit(frameRegion, "Flip Region Horizontally");
        for (int i = 0; i < frameRegion.width / 2; i++) {
            Frame lframe = clip.getFrame(frameRegion.x + i);
            Frame rframe = clip.getFrame(frameRegion.x + frameRegion.width - 1 - i);
            for (int j = frameRegion.y; j < frameRegion.y + frameRegion.height; j++) {
                double tmp = rframe.getReal(j);
                rframe.setReal(j, lframe.getReal(j));
                lframe.setReal(j, tmp);
            }
        }
        clip.endEdit();
    }

}
