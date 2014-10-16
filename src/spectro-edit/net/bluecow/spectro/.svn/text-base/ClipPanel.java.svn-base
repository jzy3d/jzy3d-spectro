/*
 * Created on Jul 14, 2008
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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.logging.Logger;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;
import javax.swing.undo.UndoableEditSupport;

/**
 * GUI component for looking at and modifying clips.
 * <p>
 * This component fires undoable edit events regarding its region
 * selection state, so it would be wise to attach it to the same
 * undo manager that the clip is attached to.
 */
public class ClipPanel extends JPanel implements Scrollable {

    private static final Logger logger = Logger.getLogger(ClipPanel.class.getName());
    
    /**
     * The clip this panel visualizes.
     */
    private final Clip clip;

    private final BufferedImage img;
    
    /**
     * The pixel data in {@link #img}.
     */
    private final int[] imgPixels;
    
    /**
     * A rectangular frame that some tools use as a bounding box for
     * the changes they make. The region feature can be turned on and off.
     */
    private Rectangle region;
    
    /**
     * The place where the region was last time it was repainted. Starts off
     * as null, and gets reset to null whenever region selection is turned off.
     */
    private Rectangle oldRegion;

    /**
     * Flag to indicate whether or not region mode is active.
     */
    private boolean regionMode;
    
    private final RegionMouseHandler mouseHandler = new RegionMouseHandler();

    private final ClipPositionHeader clipPositionHeader = new ClipPositionHeader();
    
    /**
     * Gets set to true while an undo is in progress. When true, undo
     * events are not fired.
     */
    private boolean undoing;
    
    private ValueColorizer colorizer = new LogarithmicColorizer(this);
    
    private ClipDataChangeListener clipDataChangeHandler = new ClipDataChangeListener() {

        public void clipDataChanged(ClipDataChangeEvent e) {
            Rectangle r = toScreenCoords(e.getRegion());
            updateImage(r);
            repaint(r);
        }
        
    };
    
    private final UndoableEditSupport undoSupport = new UndoableEditSupport(this);

    /**
     * Creates a new ClipPanel component for the given clip. Also attaches
     * to the given player thread, and will show a visual indication of playback
     * position for that thread.
     * 
     * @param clip
     * @param playerThread
     * @return
     */
    public static ClipPanel newInstance(Clip clip, PlayerThread playerThread) {
        ClipPanel cp = new ClipPanel(clip);
        clip.addClipDataChangeListener(cp.clipDataChangeHandler);
        playerThread.addPlaybackPositionListener(cp.clipPositionHeader);
        cp.clipPositionHeader.setPlayerThread(playerThread);
        return cp;
    }

    public static ClipPanel newInstance(Clip clip) {
        return new ClipPanel(clip);
    }
    
    private ClipPanel(Clip clip) {
        this.clip = clip;
        setPreferredSize(new Dimension(clip.getFrameCount(), clip.getFrameFreqSamples()));
        img = new BufferedImage(clip.getFrameCount(), clip.getFrameFreqSamples(), BufferedImage.TYPE_INT_RGB);
        imgPixels = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
        updateImage(null);
        setBackground(Color.BLACK);
    }

    /**
     * Converts the given point (which is in screen coordinates) to co-ordinates
     * that work with clips and their frames. The x value will be the
     * corresponding frame number within the clip, and the y value will be the
     * corresponding index within the frame's spectral data.
     * <p>
     * This is the inverse operation to {@link #toScreenCoords(Point)}.
     * 
     * @param p
     *            The point to convert. This point object will be modified!
     * @return The given point, which has been modified.
     */
    public Point toClipCoords(Point p) {
        p.y = clip.getFrameFreqSamples() - p.y;
        return p;
    }

    /**
     * Converts the given rectangle (which is in screen coordinates) to co-ordinates
     * that work with clips and their frames. The x value will be the
     * corresponding frame number within the clip, and the y value will be the
     * corresponding index within the frame's spectral data.
     * <p>
     * This is the inverse operation to {@link #toScreenCoords(Point)}.
     * <pre>
     *   . (0,0)
     *   
     *   
     *       (4,3)+------------+(10,3)
     *            |            |
     *            |            |
     *       (4,6)+------------+(10,6)  width=6 height=3
     *       
     *                                       . (8,15)
     * </pre>
     * @param p
     *            The point to convert. This point object will be modified!
     * @return The given point, which has been modified.
     */
    public Rectangle toClipCoords(Rectangle r) {
        r.y = clip.getFrameFreqSamples() - (r.y + r.height);
        return r;
    }

    /**
     * Converts the given point (which is in screen coordinates) to co-ordinates
     * that work with clips and their frames. The x value will be the
     * corresponding x coordinate on screen, and the y value will be the
     * corresponding y coordinate on screen.
     * <p>
     * This is the inverse operation to {@link #toClipCoords(Point)}.
     * 
     * @param p
     *            The point to convert. This point object will be modified!
     * @return The given point, which has been modified.
     */
    public Point toScreenCoords(Point p) {
        p.y = clip.getFrameFreqSamples() - p.y;
        return p;
    }

    public Rectangle toScreenCoords(Rectangle r) {
        // These operations are actually the same (the operation is its own inverse)
        return toClipCoords(r);
    }

    public ValueColorizer getColorizer() {
        return colorizer;
    }
    
    /**
     * Updates the image based on the existing Clip data and the settings in
     * this panel (such as the multiplier).
     * <p>
     * It is not necessary to call this method directly unless you are a
     * ValueColorizer and your settings have changed.  Image updates due to
     * clip data change events are handled automatically.
     * TODO have colorizers fire change events so this method can be private again
     * 
     * @param region
     *            The region to update, in screen co-ordinates. Null means to
     *            update the whole image.
     */
    void updateImage(Rectangle region) {
        if (clip == null) return;
        if (region == null) {
            region = new Rectangle(0, 0, clip.getFrameCount(), clip.getFrameFreqSamples());
        } else {
            region = new Rectangle(region);
        }
        
        toClipCoords(region);
        
        final int endCol = region.x + region.width;
        final int endRow = region.y + region.height;
        
        for (int col = region.x; col < endCol; col++) {
            Frame f = clip.getFrame(col);
            for (int row = region.y; row < endRow; row++) {
                // the following is a MUCH faster equivalent to: img.setRGB(col, row, greyVal);
                imgPixels[col + row * img.getWidth()] = colorizer.colorFor(f.getReal(row));
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        
        // Flip upside down while rendering the spectrogram
        AffineTransform backupTransform = g2.getTransform();
        g2.translate(0, img.getHeight());
        g2.scale(1.0, -1.0);
        
        Rectangle clipBounds = g2.getClipBounds();
        logger.finer(String.format("Clip bounds: (%d, %d) %dx%d", clipBounds.x, clipBounds.y, clipBounds.width, clipBounds.height));
        if (clipBounds.x + clipBounds.width > img.getWidth()) {
            clipBounds.width = img.getWidth() - clipBounds.x;
        }
        if (clipBounds != null) {
            g2.drawImage(img,
                    clipBounds.x, clipBounds.y, clipBounds.x + clipBounds.width, clipBounds.y + clipBounds.height,
                    clipBounds.x, clipBounds.y, clipBounds.x + clipBounds.width, clipBounds.y + clipBounds.height,
                    Color.BLACK, null);
        } else {
            g2.drawImage(img, 0, 0, null);
        }
        
        // Now flip back for the region
        g2.setTransform(backupTransform);
        if (region != null) {
            g2.setColor(Color.YELLOW);
            g2.drawRect(
                    region.x, region.y,
                    region.width, region.height);
        }
    }

    public Clip getClip() {
        return clip;
    }

    /**
     * Produces a repaint request that covers the old region as it existed last
     * time this method was called, and the new region as of now.  It shouldn't
     * be necessary to call this method directly; use {@link #setRegion(Rectangle)}.
     */
    private void repaintRegion() {
        Rectangle newRegion = (region == null) ? null : new Rectangle(region);
        if (oldRegion != null && newRegion == null) {
            repaint(oldRegion.x, oldRegion.y, oldRegion.width + 1, oldRegion.height + 1);
        } else if (oldRegion == null && newRegion != null) {
            repaint(newRegion.x, newRegion.y, newRegion.width + 1, newRegion.height + 1);
        } else if (oldRegion != null && newRegion != null) {
            oldRegion.add(newRegion);
            repaint(oldRegion.x, oldRegion.y, oldRegion.width + 1, oldRegion.height + 1);
        }
        oldRegion = newRegion;
    }

    /**
     * Turns region selection mode on or off. Attempting to set the region mode
     * to its current setting is not an error, but has no effect.
     * 
     * @param on
     *            The new setting for region mode (true for on; false for off).
     */
    public void setRegionMode(boolean on) {
        if (on != regionMode) {
            if (on) {
                regionMode = true;
                addMouseListener(mouseHandler);
                addMouseMotionListener(mouseHandler);
            } else {
                regionMode = false;
                setRegion(null);
                oldRegion = null;
                removeMouseListener(mouseHandler);
                removeMouseMotionListener(mouseHandler);
            }
        }
    }

    /**
     * Returns a copy of the currently-selected region of this clip panel. If
     * there is no region selected, returns null.
     * 
     * @return A new rectangle of this clip's selected region. The copy is
     *         independent of this clip panel--you can modify it if you wish.
     */
    public Rectangle getRegion() {
        if (region == null) {
            return null;
        } else {
            return new Rectangle(region);
        }
    }
    
    /**
     * Updates the geometry of the selected region.
     * 
     * @param r The new location and size for the selected region.
     */
    private void setRegion(Rectangle r) {
        final Rectangle oldRegion = region;
        region = normalized(r);
        repaintRegion();
        firePropertyChange("region", oldRegion, region);
        if (!undoing) {
            undoSupport.postEdit(new RegionMoveEdit(oldRegion));
        }
    }
    
    /**
     * Enumeration of states for the {@link RegionMouseHandler}.
     */
    enum MouseMode { IDLE, SIZING, MOVING }

    /**
     * Handles mouse events on this panel for purposes of creating, moving, and resizing
     * the region.
     */
    private class RegionMouseHandler implements MouseMotionListener, MouseListener {

        MouseMode mode = MouseMode.IDLE;
        
        /**
         * The offset from the region's (x,y) origin that the mouse should
         * stay at when the region is being moved.
         */
        Point moveHandle;
        
        Rectangle tempRegion;
        
//        /**
//         * The place where the user clicked to start resizing the region.
//         */
//        Point regionOrigin;
        
        public void mouseDragged(MouseEvent e) {
            switch (mode) {
            case IDLE:
                startRect(e.getPoint());
                break;
            case SIZING:
                resizeRect(e.getPoint());
                break;
            case MOVING:
                moveRect(e.getPoint());
                break;
            }
            setRegion(tempRegion);
        }

        public void mousePressed(MouseEvent e) {
            tempRegion = normalized(region);
            Point p = e.getPoint();
            if (tempRegion != null && tempRegion.contains(p)) {
                mode = MouseMode.MOVING;
                moveHandle = new Point(p.x - tempRegion.x, p.y - tempRegion.y);
            } else {
                startRect(p);
                mode = MouseMode.SIZING;
            }
            setRegion(tempRegion);
        }

        public void mouseReleased(MouseEvent e) {
            mode = MouseMode.IDLE;
            
            setRegion(tempRegion);
            tempRegion = null;
        }
        
        public void mouseMoved(MouseEvent e) {
            // don't care
        }

        public void mouseClicked(MouseEvent e) {
            // don't care
        }

        public void mouseEntered(MouseEvent e) {
            // don't care
        }

        public void mouseExited(MouseEvent e) {
            // don't care
        }
        
        private void startRect(Point p) {
            tempRegion = new Rectangle(p.x, p.y, 0, 0);
        }

        private void resizeRect(Point p) {
            tempRegion.width = p.x - tempRegion.x;
            tempRegion.height = p.y - tempRegion.y;
            logger.finer("Resizing region to: " + tempRegion);
        }

        private void moveRect(Point p) {
            tempRegion.x = p.x - moveHandle.x;
            tempRegion.y = p.y - moveHandle.y;
        }

    }

    /**
     * Creates a copy of the given rectangle with nonnegative width and
     * height. The new rectangle's actual geometry is the same as the given
     * rectangle's.
     * 
     * @param rect
     *            The source rectangle. This rectangle will not be changed
     *            as a result of this call. You can pass in null, which
     *            results in a null return value.
     * @return A rectangle with the same position and size as rect, but with
     *         nonnegative width and height. Returns null if rect is null.
     */
    private Rectangle normalized(Rectangle rect) {
        if (rect == null) return null;
        rect = new Rectangle(rect);
        if (rect.width < 0) {
            rect.x += rect.width;
            rect.width *= -1;
        }
        if (rect.height < 0) {
            rect.y += rect.height;
            rect.height *= -1;
        }
        return rect;
    }

    private final class RegionMoveEdit extends AbstractUndoableEdit {
        private Rectangle oldr;
        private final Rectangle newr = region;

        private RegionMoveEdit(Rectangle oldRegion) {
            oldr = oldRegion;
        }

        @Override
        public void undo() throws CannotUndoException {
            super.undo();
            undoing = true;
            setRegion(oldr);
            undoing = false;
        }

        @Override
        public void redo() throws CannotRedoException {
            super.redo();
            undoing = true;
            setRegion(newr);
            undoing = false;
        }

        @Override
        public boolean isSignificant() {
            return false;
        }

        @Override
        public boolean replaceEdit(UndoableEdit anEdit) {
            if (anEdit instanceof RegionMoveEdit) {
                RegionMoveEdit replaceMe = (RegionMoveEdit) anEdit;
                oldr = replaceMe.oldr;
                replaceMe.die();
                return true;
            } else {
                return false;
            }
        }
        
        @Override
        public String toString() {
            return "Region move: " + oldr + " -> " + newr;
        }
    }

    /**
     * Configures the header component in the enclosing scrollpane, if the
     * parent we were just added to is the viewport of a scrollpane.
     */
    @Override
    public void addNotify() {
        super.addNotify();
        Component p = getParent();
        if (p instanceof JViewport && p.getParent() instanceof JScrollPane) {
            JScrollPane sp = (JScrollPane) p.getParent();
            sp.setColumnHeaderView(clipPositionHeader);
        }
    }
    
    /**
     * The header component for this clip panel. Automatically installs when this
     * panel is in a JScrollPane.
     */
    private class ClipPositionHeader extends JPanel implements PlaybackPositionListener {

        private PlayerThread playerThread;
        volatile long playbackPosition;
        
        public ClipPositionHeader() {
            setPreferredSize(new Dimension(1, 20));
            addMouseListener(repositionHandler);
        }

        public void setPlayerThread(PlayerThread playerThread) {
            this.playerThread = playerThread;
        }

        public void playbackPositionUpdate(PlaybackPositionEvent e) {
            int oldPixelPosition = playbackPixelPosition();
            playbackPosition = e.getSamplePos();
            int newPixelPosition = playbackPixelPosition();
            if (newPixelPosition >= oldPixelPosition) {
                repaint(oldPixelPosition, 0, newPixelPosition - oldPixelPosition + 1, getHeight());
            } else {
                repaint(newPixelPosition, 0, oldPixelPosition - newPixelPosition + 1, getHeight());
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.BLACK);
            // TODO draw an inverted triangle instead of a rectangle
            g.drawRect(playbackPixelPosition(), 0, 1, getHeight());
        }

        /**
         * Calculates the correct x-coordinate to position the playback indicator
         * at, based on the clip's settings and the current playback position.
         */
        private int playbackPixelPosition() {
            return (int) (playbackPosition / clip.getFrameTimeSamples());
        }
        
        /**
         * Repositions the playback thread to the frame corresponding to the mouse click coordinates.
         */
        private final MouseListener repositionHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                playerThread.setPlaybackPosition(e.getX() * clip.getFrameTimeSamples());
            }
        };

    }
    
    // --------------------- Scrollable interface ------------------------

    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return (int) (visibleRect.width * 0.9);
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 50;
    }

    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    public boolean getScrollableTracksViewportWidth() {
        return false;
    }

    public void addUndoableEditListener(UndoableEditListener l) {
        undoSupport.addUndoableEditListener(l);
    }

    public void removeUndoableEditListener(UndoableEditListener l) {
        undoSupport.removeUndoableEditListener(l);
    }

    
}
