/*
 * Created on Nov 18, 2008
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

import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import net.bluecow.spectro.Clip;
import net.bluecow.spectro.ClipPanel;
import net.bluecow.spectro.SpectroEditSession;

public abstract class AbstractRegionalTool implements Tool {

    /**
     * The clip panel this tool is currently activated for.
     */
    protected ClipPanel clipPanel;
    
    protected Clip clip;

    /**
     * The current region in the clip. This value is updated upon every call to
     * {@link #regionChanged(Rectangle)}.
     */
    protected Rectangle region;

    /**
     * Sets the clip panel into region mode and installs a region change
     * listener on it. Also calls {@link #regionChanged(Rectangle)} with the
     * initial region, which could be null. If you override this method, be sure
     * to invoke this one with a super.activate(session) in your override.
     */
    public void activate(SpectroEditSession session) {
        this.clipPanel = session.getClipPanel();
        clip = clipPanel.getClip();
        clipPanel.setRegionMode(true);
        clipPanel.addPropertyChangeListener("region", clipEventHandler);
        
        regionChanged(clipPanel.getRegion());
    }

    /**
     * Removes the region change listener from this tool. If you override this
     * method, be sure to invoke this one with a super.deactivate(session) in your
     * override.
     */
    public void deactivate() {
        clipPanel.removePropertyChangeListener("region", clipEventHandler);
        clip = null;
        clipPanel = null;
    }

    /**
     * This method is called whenever the tool is active and the current region
     * of the clip has been moved or resized. You can override it if you need to
     * take action when the selected region has changed. If you override this
     * method, be sure to call super.regionChanged() in order to update the
     * {@link #region} rectangle.
     * 
     * @param region
     *            The new bounds of the selected clip region.
     */
    protected void regionChanged(Rectangle region) {
        this.region = region;
    }
    
    /**
     * Handles change events from the clip and relays them to the various
     * protected update methods.
     */
    private final PropertyChangeListener clipEventHandler = new PropertyChangeListener() {

        public void propertyChange(PropertyChangeEvent evt) {
            if ("region".equals(evt.getPropertyName())) {
                regionChanged((Rectangle) evt.getNewValue());
            }
        }
        
    };
}
