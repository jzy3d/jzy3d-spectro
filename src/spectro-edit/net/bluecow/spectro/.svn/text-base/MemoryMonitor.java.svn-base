/*
 * Created on Sep 10, 2008
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

import javax.swing.JLabel;
import javax.swing.Timer;

public class MemoryMonitor {

    private Timer timer;
    private JLabel label = new JLabel();
    
    private ActionListener timerAction = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            long megabyte = 1024 * 1024;
            long totalMemory = Runtime.getRuntime().totalMemory() / megabyte;
            long freeMemory = Runtime.getRuntime().freeMemory() / megabyte;
            long usedMemory = totalMemory - freeMemory;
            label.setText(usedMemory + "M/" + totalMemory + "M");
        }
    };
    
    public MemoryMonitor() {
        timer = new Timer(1000, timerAction);
    }
    
    public void start() {
        timer.start();
    }
    
    public void stop() {
        timer.stop();
    }

    public JLabel getLabel() {
        return label;
    }
}
