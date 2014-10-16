/*
 * Created on Aug 15, 2008
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

import java.awt.Rectangle;

/**
 * The ClipDataChangeEvent is notification of a change in data contents
 * for one or more frames of a clip.
 */
public class ClipDataChangeEvent {

    private final Clip source;
    private final Rectangle region;

    
    /**
     * @param region
     */
    public ClipDataChangeEvent(Clip source, Rectangle region) {
        this.source = source;
        this.region = region;
        if (region.width == 0 || region.height == 0) {
            throw new IllegalArgumentException(
                    "Region has 0 area (width="+region.width+
                    ", height="+region.height+")");
        }
    }

    public Clip getSource() {
        return source;
    }
    
    /**
     * Returns the region that changed.
     * 
     * @return A new copy of the rectangle describing the changed region. You
     *         are free to modify this rectangle.
     */
    public Rectangle getRegion() {
        return new Rectangle(region);
    }
    
    @Override
    public String toString() {
        return "Clip Data Change @ " + region;
    }
}
