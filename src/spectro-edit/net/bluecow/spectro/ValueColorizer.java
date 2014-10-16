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

import javax.swing.JComponent;

/**
 * ValueColorizers provide a means for showing a single value as a 24-bit RGB
 * colour. The interface only defines the single method that converts a single
 * value; implementations will provide specific configuration parameters
 * particular to their own approach.
 */
public interface ValueColorizer {

    /**
     * 
     * @param val
     * @return
     */
    public int colorFor(double val);

    public JComponent getSettingsPanel();

}
