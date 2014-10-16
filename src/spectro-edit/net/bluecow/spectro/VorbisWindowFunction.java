/*
 * Created on Jul 25, 2008
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

import java.util.Arrays;
import java.util.logging.Logger;

public class VorbisWindowFunction implements WindowFunction {

    private static final Logger logger = Logger.getLogger(VorbisWindowFunction.class.getName());
    
    private final double[] scalars;
    
    private static final double PI = Math.PI;
    
    public VorbisWindowFunction(int size) {
        scalars = new double[size];
        for (int i = 0; i < size; i++) {

            // This is the real vorbis one, but it's designed for MDCT where
            // the output array is half the size of the input array
            // double xx = Math.sin( (PI/(2.0*size)) * (i + 0.5) );
            
            double xx = Math.sin( (PI/(2.0*size)) * (2.0 * i) );
            scalars[i] = Math.sin( (PI/2.0) * (xx * xx) );
        }
        logger.finest(String.format("VorbisWindowFunction scalars (size=%d): %s\n", scalars.length, Arrays.toString(scalars)));
    }
    
    public void applyWindow(double[] data) {
        if (data.length != scalars.length) {
            throw new IllegalArgumentException(
                    "Invalid array size (required: "+scalars.length+
                    "; given: "+data.length+")");
        }
        for (int i = 0; i < data.length; i++) {
            data[i] *= scalars[i];
        }
    }

}
