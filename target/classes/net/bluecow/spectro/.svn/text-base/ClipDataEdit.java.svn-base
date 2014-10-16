/*
 * Created on Aug 14, 2008
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
import java.util.Arrays;
import java.util.logging.Logger;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

/**
 * Captures the necessary state and behaviour to undo and redo some change
 * to a rectangular region of spectral data.
 */
public class ClipDataEdit extends AbstractUndoableEdit {

    private static final Logger logger = Logger.getLogger(ClipDataEdit.class.getName());
    
    private final Clip clip;
    private final int firstFrame;
    private final int firstFreqIndex;
    private double[][] oldData;
    private double[][] newData;
    
    /**
     * @param clip The clip to capture data from and apply undo/redo operations to.
     * @param firstFrame The index of the first frame to capture.
     * @param firstFreqIndex The first frequency index to capture.
     * @param nFrames The number of frames (starting at and including firstFrame) to capture.
     * @param nFreqs The number of frequency slots (starting at and including firstFreqIndex) to capture.
     */
    public ClipDataEdit(
            Clip clip,
            int firstFrame,
            int firstFreqIndex,
            int nFrames,
            int nFreqs) {
        if (nFrames == 0) {
            throw new IllegalArgumentException("Data area to capture is empty (nFrames == 0)");
        }
        if (nFreqs == 0) {
            throw new IllegalArgumentException("Data area to capture is empty (nFreqs == 0)");
        }
        this.clip = clip;
        this.firstFrame = firstFrame;
        this.firstFreqIndex = firstFreqIndex;
        oldData = new double[nFrames][nFreqs];
        capture(oldData);
    }
    
    /**
     * Convenience method for {@link #ClipDataEdit(Clip, int, int, int, int)}
     * that passes in the given rectangle's position and size.
     * 
     * @param clip The clip to capture data from
     * @param r The region to capture
     */
    public ClipDataEdit(Clip clip, Rectangle r) {
        this(clip, r.x, r.y, r.width, r.height);
    }

    @Override
    public boolean replaceEdit(UndoableEdit anEdit) {
        boolean replace = false;
        if (anEdit instanceof ClipDataEdit) {
            ClipDataEdit other = (ClipDataEdit) anEdit;
            if (
                other.firstFrame == firstFrame &&
                other.firstFreqIndex == firstFreqIndex &&
                other.oldData.length == oldData.length &&
                other.oldData[0].length == oldData[0].length &&
                other.clip == clip) {
                replace = true;
                oldData = other.oldData;
                other.die();
            }
        }
        logger.fine("Replace edit? " + replace);
        return replace;
    }
    
    /**
     * Copies the current contents of the same clip region that was captured
     * during the constructor invocation. This will be the REDO data.
     */
    public void captureNewData() {
        if (newData != null) {
            throw new IllegalStateException("Already captured new data");
        }
        newData = new double[oldData.length][oldData[0].length];
        capture(newData);
        if (Arrays.deepEquals(oldData, newData)) {
            logger.fine("Captured new data == old data!");
        }
    }
    
    @Override
    public void undo() throws CannotUndoException {
        super.undo();
        logger.fine("Undoing edit at " + getRegion());
        apply(oldData);
        clip.regionChanged(getRegion());
    }

    @Override
    public void redo() throws CannotRedoException {
        super.redo();
        logger.fine("Redoing edit at " + getRegion());
        apply(newData);
        clip.regionChanged(getRegion());
    }
    
    /**
     * Applies the given data into the frames of {@link #clip}.
     * 
     * @param data The data to copy into clip.
     */
    private void apply(double[][] data) {
        for (int i = 0; i < data.length; i++) {
            Frame f = clip.getFrame(i + firstFrame);
            for (int j = 0; j < data[0].length; j++) {
                f.setReal(j + firstFreqIndex, data[i][j]);
            }
        }
    }

    /**
     * Copies data from the clip into the given arrays.
     * 
     * @param data The arrays to store the clip data into.
     */
    private void capture(double[][] data) {
        for (int i = 0; i < data.length; i++) {
            Frame f = clip.getFrame(i + firstFrame);
            for (int j = 0; j < data[0].length; j++) {
                data[i][j] = f.getReal(j + firstFreqIndex);
            }
        }
    }

    /**
     * Returns the data region for this edit.
     * 
     * @return A rectangle with (x, y, w, h) == (firstFrame, firstFreqIndex, nFrames, nFreqs).
     */
    public Rectangle getRegion() {
        return new Rectangle(firstFrame, firstFreqIndex, oldData.length, oldData[0].length);
    }

    /**
     * Returns true if this edit covers exactly the same region as the given
     * rectangle.
     * 
     * @param r The rectangle to check. If null, returns false.
     */
    public boolean isSameRegion(Rectangle r) {
        if (r == null) {
            return false;
        } else {
            return
                r.x == firstFrame &&
                r.y == firstFreqIndex &&
                r.width == oldData.length &&
                r.height == oldData[0].length;
        }
    }

    /**
     * Returns the old data. Modifications to the returned array will affect the
     * stored undo data, so client code should avoid modifying it.
     */
    public double[][] getOldData() {
        return oldData;
    }
    
    @Override
    public String toString() {
        return String.format(
                "Clip Data Edit @ [%d, %d %d x %d]",
                firstFrame, firstFreqIndex, oldData.length, oldData[0].length);
    }
}
