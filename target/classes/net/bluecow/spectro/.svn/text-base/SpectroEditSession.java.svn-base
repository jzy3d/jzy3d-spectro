/*
 * Created on Jul 16, 2008
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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import net.bluecow.spectro.action.PlayPauseAction;
import net.bluecow.spectro.action.RewindAction;
import net.bluecow.spectro.action.SaveAction;
import net.bluecow.spectro.action.UndoRedoAction;
import net.bluecow.spectro.tool.ToolboxPanel;

/**
 * The central point of coordination for all the parts that work together to
 * provide a GUI editing environment for an audio file.
 * <p>
 * This class also provides the main() method that is used to launch the application.
 *
 * @author fuerth
 */
public class SpectroEditSession {

    private static final Logger logger = Logger.getLogger(SpectroEditSession.class.getName());
    
    /**
     * The preferences object we store session preferences in.
     */
    private static final Preferences prefs = Preferences.userNodeForPackage(SpectroEditSession.class);

    /**
     * The undo manager that keeps track of changes in this session, including
     * the clip data and the state of various tools.
     */
    private final UndoManager undoManager = new UndoManager();

    private final PlayerThread playerThread;

    private final ClipPanel clipPanel;
    
    protected SpectroEditSession(Clip c) throws LineUnavailableException {
        playerThread = new PlayerThread(c);
        playerThread.start();

        clipPanel = ClipPanel.newInstance(c, playerThread);
        clipPanel.addUndoableEditListener(undoManager);
        
        final JFrame f = new JFrame("Spectro-Edit " + Version.VERSION);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLayout(new BorderLayout());

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setTopComponent(new ToolboxPanel(this).getPanel());
        splitPane.setBottomComponent(new JScrollPane(clipPanel));
        f.add(splitPane, BorderLayout.CENTER);

        JToolBar toolbar = new JToolBar();
        toolbar.add(new SaveAction(c, f));
        toolbar.add(UndoRedoAction.createUndoInstance(undoManager));
        toolbar.add(UndoRedoAction.createRedoInstance(undoManager));
        toolbar.addSeparator();
        toolbar.add(new PlayPauseAction(playerThread));
        toolbar.add(new RewindAction(playerThread));
        f.add(toolbar, BorderLayout.NORTH);
        
        if (prefs.get("frameBounds", null) != null) {
            String[] frameBounds = prefs.get("frameBounds", null).split(",");
            if (frameBounds.length == 4) {
                f.setBounds(
                        Integer.parseInt(frameBounds[0]),
                        Integer.parseInt(frameBounds[1]),
                        Integer.parseInt(frameBounds[2]),
                        Integer.parseInt(frameBounds[3]));
            }
        } else {
            f.pack();
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            f.setSize(
                    Math.min(screenSize.width - 50, f.getWidth()),
                    Math.min(screenSize.height - 50, f.getHeight()));
            f.setLocationRelativeTo(null);
        }
        f.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    prefs.put("frameBounds", String.format("%d,%d,%d,%d", f.getX(), f.getY(), f.getWidth(), f.getHeight()));
                    prefs.flush();
                } catch (BackingStoreException ex) {
                    logger.log(Level.WARNING, "Failed to flush preferences", ex);
                }
            }
        });
        f.setVisible(true);
    }
    
    /**
     * Creates a new session with a GUI for editing a Clip. The Clip's contents
     * will be initialized to correspond with the given file, which should be
     * in a PCM-encoded WAV or AIFF file.
     * 
     * @param wavFile The file to load
     * @return The new session that was created
     * @throws UnsupportedAudioFileException
     * @throws IOException
     * @throws LineUnavailableException
     */
    public static SpectroEditSession createSession(File wavFile) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        Clip c = Clip.newInstance(wavFile);
        SpectroEditSession session = new SpectroEditSession(c);
        c.addUndoableEditListener(session.undoManager);
        return session;
    }
    
    /**
     * Launches the Spectro-Edit application by prompting the user for a file, then
     * creating a new session for editing that file.
     * 
     * @param args Currently ignored
     * @throws Exception If startup fails catastrophically
     */
    public static void main(String[] args) throws Exception {
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Spectro-Edit");
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        LogManager.getLogManager().readConfiguration(SpectroEditSession.class.getResourceAsStream("LogManager.properties"));
        final JFrame f = new JFrame("Dummy frame for owning dialogs");
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    FileDialog fd = new FileDialog(f, "Choose a 16-bit WAV or AIFF file");
                    fd.setVisible(true);
                    String dir = fd.getDirectory();
                    String file = fd.getFile();
                    if (dir == null || file == null) {
                        JOptionPane.showMessageDialog(f, "Ok, maybe next time");
                        System.exit(0);
                    }
                    File wavFile = new File(dir, file);
                    createSession(wavFile);
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(f,
                            "Sorry, couldn't read your sample:\n" +
                            e.getMessage() +
                            "\nBe sure your file is 16-bit WAV or AIFF!");
                    System.exit(0);
                }
            }
        });
    }
    
    public void undo() {
        undoManager.undo();
    }
    
    public void redo() {
        undoManager.redo();
    }

    public ClipPanel getClipPanel() {
        return clipPanel;
    }
    
    public UndoManager getUndoManager() {
        return undoManager;
    }
}
