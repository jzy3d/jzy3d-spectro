/*
 * Created on Jul 27, 2008
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

import java.util.LinkedList;

public class OverlapBuffer {

    /**
     * The buffers this overlap manager is managing. New ones are added to the
     * front of the list, and old ones are removed from the end. There are always
     * the same number of buffers in this list as the overlap amount given in the
     * constructor. For instance, if overlap=2, there are 2 buffers in this list.
     */
    private final LinkedList<double[]> buffers;
    
    /**
     * A frame of all-0 samples for padding the beginning and end of the stream.
     */
    private final double[] emptyFrame;
    
    /**
     * The number of samples to offset the current index by when adding the sample
     * from the next frame.
     */
    private final int offset;
    
    /**
     * The current sample in the first queued frame.
     */
    private int current;
    
    /**
     * Overlap 2:
     * <pre>
     *   Frame 0 ========
     *   Frame 1     ========
     *   Frame 2         ========
     *   Frame 3             ========
     * </pre>
     * 
     * @param frameSize
     * @param overlap
     */
    public OverlapBuffer(int frameSize, int overlap) {
        offset = frameSize / overlap;
        emptyFrame = new double[frameSize];
        
        buffers = new LinkedList<double[]>();
        for (int i = 0; i < overlap; i++) {
            buffers.add(emptyFrame);
        }
    }
    
    public double next() {
        int myOffset = current;
        double val = 0.0;
        for (double[] buf : buffers) {
//        for (int i = buffers.size() -1; i >= 0; i--) {
//            double[] buf = ((LinkedList<double[]>) buffers).get(i);
            val += buf[myOffset];
            myOffset += offset;
        }
        current++;
        return val;
    }
    
    public void addFrame(double[] frame) {
        buffers.addFirst(frame);
        buffers.removeLast();
        current = 0;
    }
    
    /**
     * Works like {@link #addFrame(double[])} with a frame argument of all 0.
     */
    public void addEmptyFrame() {
        addFrame(emptyFrame);
    }
    
    public boolean needsNewFrame() {
        return current == offset;
    }
}
