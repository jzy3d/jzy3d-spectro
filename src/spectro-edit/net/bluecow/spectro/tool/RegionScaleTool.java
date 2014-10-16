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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.bluecow.spectro.Clip;
import net.bluecow.spectro.ClipDataEdit;
import net.bluecow.spectro.ClipPanel;
import net.bluecow.spectro.Frame;
import net.bluecow.spectro.SpectroEditSession;

public class RegionScaleTool implements Tool {

    private ClipPanel clipPanel;
    private Clip clip;

    private final PropertyChangeListener clipEventHandler = new PropertyChangeListener() {

        public void propertyChange(PropertyChangeEvent evt) {
            if ("region".equals(evt.getPropertyName())) {
//                if (origData != null) {
//                    clip.endEdit();
//                    origData = null;
//                }
                scaleSlider.setValue(initialScale);
            }
        }
        
    };
    
    /**
     * The data we've captured. This is what the data in the selected region
     * looked like when we started, and it's what we scale when the slider
     * moves.
     * TODO if we could ask Clip for the undo data of its current in-progress
     * edit, we would not need this
     */
    private ClipDataEdit origData;
    
    private final Box settingsPanel;
    private final JSlider scaleSlider;
    private final int initialScale = 100;
    
    public RegionScaleTool() {
        settingsPanel = Box.createVerticalBox();
        settingsPanel.add(new JLabel("Scale amount"));

        scaleSlider = new JSlider(0, 500, 100);
        scaleSlider.setOpaque(false);
        scaleSlider.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                if (scaleSlider.getValueIsAdjusting()) {
                    scaleRegion(scaleSlider.getValue() / 100.0);
                }
                // TODO fire undo event for slider position
            }
            
        });
        settingsPanel.add(scaleSlider);
        
        settingsPanel.add(Box.createGlue());
    }

    public String getName() {
        return "Scale Region";
    }
    
    public void activate(SpectroEditSession session) {
        this.clipPanel = session.getClipPanel();
        clip = clipPanel.getClip();
        clipPanel.setRegionMode(true);
        clipPanel.addPropertyChangeListener("region", clipEventHandler);
    }

    public void deactivate() {
        if (origData != null) {
//            clip.endEdit();
            origData = null;
        }
        clipPanel.removePropertyChangeListener("region", clipEventHandler);
        clip = null;
        clipPanel = null;
    }

    public JComponent getSettingsPanel() {
        return settingsPanel;
    }

    @Override
    public String toString() {
        return "Scale Region";
    }
    
    /**
     * Scales the actual clip data in the given region by the amount given. 1.0 means
     * no change; between 0.0 and 1.0 means to reduce intensity, and &gt;1.0 means
     * to increase intensity.
     */
    public void scaleRegion(double amount) {
        Rectangle region = clipPanel.getRegion();
        if (region == null || region.width == 0 || region.height == 0) {
            origData = null;
            return;
        }
        Rectangle frameRegion = clipPanel.toClipCoords(new Rectangle(region));
        if (origData == null || !origData.isSameRegion(frameRegion)) {
            origData = new ClipDataEdit(clip, frameRegion);
//            clip.beginEdit(frameRegion, "Scale Region");
        }
        clip.beginEdit(frameRegion, "Scale Region");
        double[][] orig = origData.getOldData();
        for (int i = frameRegion.x; i < frameRegion.x + frameRegion.width; i++) {
            Frame frame = clip.getFrame(i);
            for (int j = frameRegion.y; j < frameRegion.y + frameRegion.height; j++) {
                frame.setReal(j, orig[i - frameRegion.x][j - frameRegion.y] * amount);
            }
        }
//        clip.regionChanged(frameRegion);
        clip.endEdit();
    }
    
}
