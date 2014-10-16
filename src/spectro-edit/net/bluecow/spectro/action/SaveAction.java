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

import java.awt.Component;
import java.awt.Dialog;
import java.awt.FileDialog;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import net.bluecow.spectro.Clip;

public class SaveAction extends AbstractAction {

    /**
     * Controls whether or not this action should prompt when the user attempts
     * to overwrite an existing file. The OS X file dialog does this
     * automatically, so this flag causes the overwrite prompt to be suppressed
     * on that platform.
     */
    private static final boolean PROMPT_ON_OVERWRITE = System.getProperty("mrj.version") != null;
    
    private final Clip clip;
    private final Component dialogOwner;

    public SaveAction(Clip clip, Component dialogOwner) {
        super("Save...");
        this.clip = clip;
        this.dialogOwner = dialogOwner;
        if (dialogOwner == null) {
            throw new NullPointerException(
                    "You have to specify an owning component for the save dialog");
        }
    }

    public void actionPerformed(ActionEvent e) {
        try {
            FileDialog fd;
            Window owner;
            if (dialogOwner instanceof Window) {
                owner = (Window) dialogOwner;
            } else {
                owner = SwingUtilities.getWindowAncestor(dialogOwner);
            }
            if (owner instanceof java.awt.Frame) {
                fd = new FileDialog((java.awt.Frame) owner, "Save sample as", FileDialog.SAVE);
            } else {
                fd = new FileDialog((Dialog) owner, "Save sample as", FileDialog.SAVE);
            }
            File targetFile = null;
            boolean promptAgain;
            do {
                promptAgain = false;
                fd.setVisible(true);
                String dir = fd.getDirectory();
                String fileName = fd.getFile();
                if (fileName == null) return;
                if (!fileName.toLowerCase().endsWith(".wav")) {
                    fileName += ".wav";
                }
                targetFile = new File(dir, fileName);
                if (PROMPT_ON_OVERWRITE && targetFile.exists()) {
                    int choice = JOptionPane.showOptionDialog(
                            owner, "The file " + targetFile + " exists.\nDo you want to replace it?",
                            "File exists", -1, JOptionPane.WARNING_MESSAGE, null,
                            new String[] { "Replace", "Cancel" }, "Replace");
                    if (choice == 0) {
                        promptAgain = false;
                    } else if (choice == 1) {
                        promptAgain = true;
                    } else if (choice == -1) {
                        return;
                    } else {
                        throw new RuntimeException("Unrecognized choice: " + choice);
                    }
                }
            } while (promptAgain);
            AudioSystem.write(
                    clip.getAudio(),
                    AudioFileFormat.Type.WAVE,
                    targetFile);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
