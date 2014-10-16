/*
 * Created on Sep 4, 2008
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

import javax.swing.JSlider;

/**
 * Simple extension to JSlider that provides a nonlinear (curved) mapping of
 * knob position to value. Also supports floating point values rather than
 * simply integers. To get the curved value, call the {@link #getCurvedValue()}
 * method. The {@link #getValue()} method returns a raw linear, scaled value.
 */
public class CurvedSlider extends JSlider {

    /**
     * Number of steps the jslider has. Its range is 0..RESOLUTION.
     */
    private static final int RESOLUTION = 100;
    
    private final double exponent;
    
    /**
     * A correction factor based on max and exponent
     */
    private final double scalar;

    /**
     * Creates a new curved slider with the given values.
     * 
     * @param min The minimum curved value that will be returned.
     * @param max The maximum curved value that will be returned.
     * @param value The initial curved value.
     * @param curveSeverity the amount of curviness the slider's range should
     * have. Larger values mean finer control over low values and a sharper
     * transition to big steps toward the top of the slider's range. Useful
     * values lie between 1.0 (linear) and 4.0 (very curved).
     */
    public CurvedSlider(double min, double max, double curviness) {
        super(0, RESOLUTION, 0);
        this.exponent = curviness;
        
        // the equation is y=(c*x)^b
        // b is "curviness" or "exponent"
        // c is (max^(1/b))/resolution  -- this ensures the max value is max, regardless of the value of b or resolution
        // y is getCurvedValue()
        // x is getValue()
        
        scalar = (Math.pow(max, 1.0/exponent)) / RESOLUTION;
    }
    
    /**
     * Returns the current slider value after applying the curve and
     * resolution settings.
     */
    public double getCurvedValue() {
        return Math.pow(scalar * getValue(), exponent);
    }
}
