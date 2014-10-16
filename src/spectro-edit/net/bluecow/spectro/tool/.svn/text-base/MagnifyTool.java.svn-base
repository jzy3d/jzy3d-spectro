/*
 * Created on Nov 5, 2008
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
import java.awt.Rectangle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import net.bluecow.spectro.Clip;
import net.bluecow.spectro.ClipPanel;
import net.bluecow.spectro.Util;

/**
 * The MagnifyTool shows a close-up of a particular region by performing a more
 * detailed spectral analysis on the underlying audio data. This will eventually
 * be turned into a first-class feature of the app (it will just be a zoom
 * operation), and the zoomed view will be modifiable just like the overall
 * view.
 * 
 * @author fuerth
 */
public class MagnifyTool extends AbstractRegionalTool {

    SpinnerNumberModel freqModel = new SpinnerNumberModel(1024, 128, Integer.MAX_VALUE, 1) {
        
        private int currentPowerOfTwo = 10;
        
        @Override
        public Object getNextValue() {
            currentPowerOfTwo++;
            return (int) Math.pow(2, currentPowerOfTwo);
        }
        
        @Override
        public Object getPreviousValue() {
            currentPowerOfTwo--;
            return (int) Math.pow(2, currentPowerOfTwo);
        }
    };
    
    private final Box settingsPanel;
    
    private final Action magnifyAction = new AbstractAction("Magnify") {
        public void actionPerformed(java.awt.event.ActionEvent e) { showMagDialog(); }
    };
    
    private JSpinner timeMagnificationSpinner = new JSpinner();
    private JSpinner frequencyResolutionSpinner = new JSpinner(freqModel);
    
    public MagnifyTool() {
        settingsPanel = Box.createVerticalBox();
        settingsPanel.setBackground(Color.WHITE);
        
        settingsPanel.add(new JLabel("Time resolution"));
        timeMagnificationSpinner.setValue(Integer.valueOf(2));
        settingsPanel.add(timeMagnificationSpinner);

        settingsPanel.add(new JLabel("Frequency resolution"));
        settingsPanel.add(frequencyResolutionSpinner);
        
        JButton magnifyButton = new JButton(magnifyAction);
        settingsPanel.add(magnifyButton);
        
        settingsPanel.add(Box.createVerticalGlue());
    }
    
    @Override
    protected void regionChanged(Rectangle region) {
        super.regionChanged(region);
        magnifyAction.setEnabled(region != null);
    }
    
    public String getName() {
        return "Magnify";
    }

    public JComponent getSettingsPanel() {
        return settingsPanel;
    }
    
    private void showMagDialog() {
        int frameSize = (Integer) frequencyResolutionSpinner.getValue();
        int overlap = (Integer) timeMagnificationSpinner.getValue();
        Clip magClip = clip.subClip(region.x, region.width, frameSize, overlap);
        ClipPanel magPanel = ClipPanel.newInstance(magClip);
        JDialog d = Util.makeOwnedDialog(clipPanel, "Magnification");
        d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        d.setContentPane(new JScrollPane(magPanel));
        d.pack();
        d.setLocationRelativeTo(clipPanel);
        d.setVisible(true);
    }

}
