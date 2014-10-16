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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.bluecow.spectro.Clip;
import net.bluecow.spectro.ClipDataEdit;
import net.bluecow.spectro.ClipPanel;
import net.bluecow.spectro.Frame;
import net.bluecow.spectro.SpectroEditSession;

/**
 * Tool that sets any data point that was over a threshold amount to 0,
 * leaving all other data points at their original values. This is a
 * nice way to remove some sound that is significantly louder than the
 * background noise, leaving only what was in the background.
 */
public class RegionThresholdTool implements Tool {

    private ClipPanel clipPanel;
    private Clip clip;

    private final PropertyChangeListener clipEventHandler = new PropertyChangeListener() {

        public void propertyChange(PropertyChangeEvent evt) {
            if ("region".equals(evt.getPropertyName())) {
                thresholdSlider.setValue(initialThreshold);
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
    private final CurvedSlider thresholdSlider;
    private final int initialThreshold = 100;

    /**
     * If checked, this tool imposes an upper threshold (removes loud parts); if
     * false it is a lower threshold (removes quiet parts).
     */
    private final JCheckBox upper;


    
    public RegionThresholdTool() {
        settingsPanel = Box.createVerticalBox();
        settingsPanel.add(new JLabel("Cutoff Threshold"));

        thresholdSlider = new CurvedSlider(0.0, 10.0, 3);
        thresholdSlider.setOpaque(false);
        thresholdSlider.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                if (thresholdSlider.getValueIsAdjusting()) {
                    applyRegionThreshold(thresholdSlider.getCurvedValue());
                }
                // TODO fire undo event for slider position
            }

        });
        settingsPanel.add(thresholdSlider);
        
        upper = new JCheckBox("Upper Threshold");
        upper.setOpaque(false);
        upper.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // just re-apply.. the method is sensitive to the state of the checkbox
                applyRegionThreshold(thresholdSlider.getCurvedValue());
            }
        });
        settingsPanel.add(upper);
        
        settingsPanel.add(Box.createGlue());
    }

    public String getName() {
        return "Region Threshold";
    }
    
    public void activate(SpectroEditSession session) {
        this.clipPanel = session.getClipPanel();
        clip = clipPanel.getClip();
        clipPanel.setRegionMode(true);
        clipPanel.addPropertyChangeListener("region", clipEventHandler);
    }

    public void deactivate() {
        origData = null;
        clipPanel.removePropertyChangeListener("region", clipEventHandler);
        clip = null;
        clipPanel = null;
    }

    public JComponent getSettingsPanel() {
        return settingsPanel;
    }


    private void applyRegionThreshold(double threshold) {
        Rectangle region = clipPanel.getRegion();
        if (region == null || region.width == 0 || region.height == 0) {
            origData = null;
            return;
        }
        Rectangle frameRegion = clipPanel.toClipCoords(new Rectangle(region));
        if (origData == null || !origData.isSameRegion(frameRegion)) {
            origData = new ClipDataEdit(clip, frameRegion);
        }
        clip.beginEdit(frameRegion, "Region Threshold");
        double[][] orig = origData.getOldData();
        for (int i = frameRegion.x; i < frameRegion.x + frameRegion.width; i++) {
            Frame frame = clip.getFrame(i);
            for (int j = frameRegion.y; j < frameRegion.y + frameRegion.height; j++) {
                double origVal = orig[i - frameRegion.x][j - frameRegion.y];
                if (upper.isSelected() && Math.abs(origVal) > threshold) {
                    frame.setReal(j, 0.0);
                } else if ( (!upper.isSelected()) && Math.abs(origVal) < threshold) {
                    frame.setReal(j, 0.0);
                } else {
                    frame.setReal(j, origVal);
                }
            }
        }
        clip.endEdit();
    }
    
}
