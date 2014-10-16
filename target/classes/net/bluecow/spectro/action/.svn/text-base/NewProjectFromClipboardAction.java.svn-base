/*
 * Created on Oct 29, 2008
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

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.io.InputStream;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import sun.misc.HexDumpEncoder;

/**
 * An action that attempts to create a new session based on the (audio) contents
 * of the system clipboard. This should be a great way to pull a segment of
 * audio out of a project in another program, clean it up, and then replace the
 * original segment with the cleaned up version. The problem is, I can't find
 * any programs that put audio data on the OS X system clipboard.
 */
public class NewProjectFromClipboardAction extends AbstractAction {

    public NewProjectFromClipboardAction() {
        super("New Project From Clipboard...");
    }
    
    /**
     * Currently just pops up a list of data flavours available for the current
     * clipboard item.
     */
    public void actionPerformed(ActionEvent e) {
        Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
        JList flavourList = new JList(cb.getAvailableDataFlavors());
        JDialog d = new JDialog((JFrame) null, "Flavours on the clipboard");
        d.setContentPane(new JScrollPane(flavourList));
        d.setModal(true);
        d.pack();
        d.setVisible(true);
        d.dispose();
        
        if (flavourList.getSelectedValue() != null) {
            DataFlavor f = (DataFlavor) flavourList.getSelectedValue();
            if (f.isRepresentationClassInputStream()) {
                try {
                    InputStream in = (InputStream) cb.getData(f);
                    HexDumpEncoder hde = new HexDumpEncoder();
                    hde.encode(in, System.out);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                NewProjectFromClipboardAction a = new NewProjectFromClipboardAction();
                a.actionPerformed(null);
            }
        });
    }
}
