/*
 * Created on Jul 17, 2008
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

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;

public class Util {

    /**
     * Converts the given array of numbers to an array of complex numbers
     * where all the imaginary parts are 0.  The
     * array size is [real.length][2], where each sample is complex; array[n][0] is the
     * real part, array[n][1] is the imaginary part of sample n.
     */
    public static double[][] realToComplex(double[] real) {
        double[][] complex = new double[real.length][2];
        for (int i = 0; i < real.length; i++) {
            complex[i][0] = real[i];
        }
        return complex;
    }
    
    /**
     * Tries very hard to create a JDialog which is owned by the parent
     * Window of the given component.  However, if the component does not
     * have a Window ancestor, or the component has a Window ancestor that
     * is not a Frame or Dialog, this method instead creates an unparented
     * JDialog which is always-on-top.
     * 
     * @param owningComponent The component that should own this dialog.
     * @param title The title for the dialog.
     * @return A JDialog that is either owned by the Frame or Dialog ancestor of
     * owningComponent, or not owned but set to be alwaysOnTop.
     */
    public static JDialog makeOwnedDialog(Component owningComponent, String title) {
        Window owner = SwingUtilities.getWindowAncestor(owningComponent);
        if (owner instanceof Frame) {
            return new JDialog((Frame) owner, title);
        } else if (owner instanceof Dialog) {
            return new JDialog((Dialog) owner, title);
        } else {
            JDialog d = new JDialog();
            d.setTitle(title);
            d.setAlwaysOnTop(true);
            return d;
        }
    }

}
