/*
 * Created on Aug 28, 2008
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
package net.bluecow.spectro;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.bluecow.spectro.tool.CurvedSlider;

public class LogarithmicColorizer implements ValueColorizer {

    private double preMult = 0;
    private double brightness = 0;
    private double contrast = 0;
    private boolean useRed = false;
    
    private final ClipPanel clipPanel;

    private final JComponent settingsPanel;

    LogarithmicColorizer(ClipPanel clipPanel) {
        this.clipPanel = clipPanel;
        
        final CurvedSlider preMultSlider = new CurvedSlider(0.0, 7000.0, 4.0);
        preMultSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                setPreMult(preMultSlider.getCurvedValue());
            }
        });

        final JSlider brightnessSlider = new JSlider(-300, 300, 0);
        brightnessSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                setBrightness(brightnessSlider.getValue());
            }
        });

        final CurvedSlider contrastSlider = new CurvedSlider(0.0, 10000.0, 4.0);
        contrastSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                setContrast(contrastSlider.getCurvedValue());
            }
        });

        final JCheckBox useRedCheckbox = new JCheckBox("Use red", useRed);
        useRedCheckbox.setOpaque(false);
        useRedCheckbox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setUseRed(useRedCheckbox.isSelected());
            }
        });
        
        settingsPanel = Box.createVerticalBox();
        settingsPanel.add(new JLabel("Pre Multiplier"));
        settingsPanel.add(preMultSlider);
        settingsPanel.add(new JLabel("Brightness"));
        settingsPanel.add(brightnessSlider);
        settingsPanel.add(new JLabel("Contrast"));
        settingsPanel.add(contrastSlider);
        settingsPanel.add(useRedCheckbox);
        
        preMultSlider.setValue(20);
        brightnessSlider.setValue(0);
        contrastSlider.setValue(50);
    }

    public int colorFor(double val) {
        int greyVal = (int) (brightness + (contrast * Math.log1p(Math.abs(preMult * val))));
        
        if (useRed) {
            if (greyVal < 0) {
                return 0;
            } else if (greyVal <= 255) {
                return (greyVal << 16) | (greyVal << 8) | (greyVal);
            } else if (greyVal <= 512) {
                greyVal -= 256;
                greyVal = 256 - greyVal;
                return 0xff0000 | (greyVal << 8) | (greyVal);
            } else {
                return 0xff0000;
            }
        } else {
            greyVal = Math.min(255, Math.max(0, greyVal));
            return (greyVal << 16) | (greyVal << 8) | (greyVal);
        }
        
    }

    public void setPreMult(double multiplier) {
        System.out.println("multiplier: " + multiplier);
        this.preMult = multiplier;
        clipPanel.updateImage(null);
        clipPanel.repaint();
    }

    public void setBrightness(double brightness) {
        System.out.println("brightness: " + brightness);
        this.brightness = brightness;
        clipPanel.updateImage(null);
        clipPanel.repaint();
    }

    public void setContrast(double contrast) {
        System.out.println("contrast: " + contrast);
        this.contrast = contrast;
        clipPanel.updateImage(null);
        clipPanel.repaint();
    }

    public void setUseRed(boolean useRed) {
        this.useRed = useRed;
        clipPanel.updateImage(null);
        clipPanel.repaint();
    }

    public JComponent getSettingsPanel() {
        return settingsPanel;
    }
}
