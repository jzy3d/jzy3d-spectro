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

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JLabel;

/**
 * Manages a component that shows the current mouse position in terms of time and frequency.
 */
public class PositionReadout {

    private final ClipPanel cp;

    private final JLabel label = new JLabel();
    
    private final MouseHandler mouseHandler = new MouseHandler();
        
    public PositionReadout(ClipPanel cp) {
        this.cp = cp;
        cp.addMouseMotionListener(mouseHandler);
        cp.addMouseListener(mouseHandler);
        
        // set up initial label properties
        mouseHandler.mouseExited(null);
    }
    
    private class MouseHandler implements MouseListener, MouseMotionListener {

        public void mouseDragged(MouseEvent e) {
            mouseMoved(e);
        }

        public void mouseMoved(MouseEvent e) {
            double rate = cp.getClip().getSamplingRate();
            double fSamples = cp.getClip().getFrameFreqSamples();
            double tSamples = cp.getClip().getFrameTimeSamples();
            Point p = cp.toClipCoords(e.getPoint());
            double freq = ((rate / 2.0) / fSamples) * p.getY();
            label.setText(String.format("0:%06.03fs %6.0fHz (%s)", p.getX() * tSamples / rate, freq, toNoteName(freq)));
            label.setEnabled(true);
        }

        public void mouseExited(MouseEvent e) {
            label.setText("(No current position)");
            label.setEnabled(false);
        }
        
        public void mouseEntered(MouseEvent e) { /* no op */ }
        public void mouseClicked(MouseEvent e) { /* no op */ }
        public void mousePressed(MouseEvent e) { /* no op */ }
        public void mouseReleased(MouseEvent e) { /* no op */ }
    };
    
    /**
     * @param freq
     * @return
     */
    private static String toNoteName(double freq) {
        // Semitones is offset by 4*12 so that 440Hz == A4
        int semitones = (int) ((4*12) + 12.0 * log2(freq/440.0));
        String[] notes = { "A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#" };
        String note = notes[semitones % 12];
        String octave = String.valueOf(semitones / 12); 
        return note+octave;
    }
    
    /**
     * Computes the base-2 logarithm of a.
     */
    private static double log2(double a) {
        // change of base formula: log_b(a) = log_c(a)/log_c(b)
        return Math.log(a) / Math.log(2);
    }
    
    public JLabel getLabel() {
        return label;
    }
    
}
