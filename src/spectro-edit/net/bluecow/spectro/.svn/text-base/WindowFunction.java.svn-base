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

/**
 * WindowFunction represents an algorithm for shaping an array of
 * data by scaling each of its values in a particular way.
 * <p>
 * Once a WindowFunction instance has been created, all data arrays
 * given to it must be the same length as each other.
 */
public interface WindowFunction {

    /**
     * Shapes the data in the given array in place, according to the
     * rules of this window function.
     * 
     * @param data The data to scale. The values in this array will be
     * modified.
     * @throws IllegalArgumentException if the array length differs from
     * the expected array length (length may be declared in the implementation's
     * constructor, or inferred from the first array passed to this instance.
     */
    void applyWindow(double[] data);
}
