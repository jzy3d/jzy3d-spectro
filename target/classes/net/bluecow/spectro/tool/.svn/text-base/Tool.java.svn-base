/*
 * Created on Aug 12, 2008
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

import javax.swing.JComponent;

import net.bluecow.spectro.SpectroEditSession;

/**
 * Defines all the things every tool can do.
 */
public interface Tool {

    /**
     * Returns the component that manages all the settings for this tool.
     */
    JComponent getSettingsPanel();
    
    /**
     * Returns the name of this tool as it should be shown to the user.
     */
    String getName();
    
    /**
     * Makes this tool respond to user input activity on the given clip panel.
     */
    void activate(SpectroEditSession session);
    
    /**
     * Makes this tool stop responding to user activity on the clip panel it was
     * previously activated on.
     */
    void deactivate();
}
