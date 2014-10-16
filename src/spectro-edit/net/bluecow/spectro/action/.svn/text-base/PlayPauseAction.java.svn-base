/*
 * Created on Aug 19, 2008
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
package net.bluecow.spectro.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.bluecow.spectro.PlayerThread;

public class PlayPauseAction extends AbstractAction {

    private final PlayerThread playerThread;
    
    private final ChangeListener playerStateHandler = new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    if (playerThread.isPlaying()) {
                        putValue(NAME, "Pause");
                    } else {
                        putValue(NAME, "Play");
                    }
                };
            });
        }
    };

    public PlayPauseAction(PlayerThread playerThread) {
        super("Play");
        this.playerThread = playerThread;
        playerThread.addChangeListener(playerStateHandler);
    }

    public void actionPerformed(ActionEvent e) {
        if (playerThread.isPlaying()) {
            playerThread.stopPlaying();
        } else {
            playerThread.startPlaying();
        }
    }
}
