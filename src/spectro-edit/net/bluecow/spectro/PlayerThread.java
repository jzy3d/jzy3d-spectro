/*
 * Created on Jul 22, 2008
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class PlayerThread extends Thread {
    
    private static final Logger logger = Logger.getLogger(PlayerThread.class.getName());

    /**
     * Whether this thread should currently be playing audio. When true,
     * playback proceeds; when false, playback is paused. Use the
     * {@link #startPlaying(boolean)} and {@link #stopPlaying()} methods to
     * manipulate this variable, because they properly handle
     * synchronization between threads.
     */
    private boolean playing = false;
    
    /**
     * When true, this thread will terminate at its earliest opportunity.
     * Once terminated, it cannot be restarted. Use the {@link #terminate()}
     * method to set this flag, because it properly handles synchronization
     * between threads.
     */
    private boolean terminated = false;

    /**
     * The output line that actual playback goes to.
     */
    private SourceDataLine outputLine;

    /**
     * The clip we're playing back samples from.
     */
    private final Clip clip;

    /**
     * The input stream that provides audio samples from the Clip.
     */
    private AudioInputStream in;
    
    /**
     * The amount that has to be subtracted from outputLine's frame position
     * in order to determine the frame position from the beginning of the clip.
     */
    private long outputLinePositionOffset;

    /**
     * The number of samples from the beginning of the clip that playback commenced
     * at. This is used as an adjustment to {@link #outputLinePositionOffset} and
     * the output line's current playback position when calculating {@link #getPlaybackPosition()}.
     */
    private int startSample;
    
    /**
     * Creates a new player thread for the given clip. Remember to call start() on
     * this thread to make it start working.
     * 
     * @param clip The clip to play back audio samples from.
     * @throws LineUnavailableException If it is not possible to open an audio device
     * for playback.
     */
    public PlayerThread(Clip clip) throws LineUnavailableException {
        this.clip = clip;
    }
    
    @Override
    public void run() {
        if (in == null) {
            setPlaybackPosition(0);
        }
        try {
            AudioFormat outputFormat = in.getFormat();
            outputLine = AudioSystem.getSourceDataLine(outputFormat);
            logger.finer("Output line buffer: "+outputLine.getBufferSize());
            outputLine.open();

            byte[] buf = new byte[outputLine.getBufferSize()];

            while (!terminated) {
                
                boolean reachedEOF = false;
                logger.info("playback starting: reachedEOF="+reachedEOF+" playing="+playing+" terminated="+terminated);
                fireStateChanged();
                outputLine.start();
                
                while (playing && !reachedEOF) {
                    synchronized (this) {
                        int readSize = (Math.min(outputLine.available(), 4096));
                        int len = in.read(buf, 0, readSize);
                        if (len != readSize) {
                            logger.fine(String.format("Didn't read full %d bytes (got %d)\n", readSize, len));
                        }
                        if (len == -1) {
                            reachedEOF = true;
                        } else {
                            outputLine.write(buf, 0, len);
                        }
                    }
                    firePlaybackPositionUpdate(getPlaybackPosition());
                }

                if (playing) {
                    // this is due to an EOF on the input data
                    // can't use outputLine.drain() here because we are responsible for firing playback position events
                    logger.finer("Draining output line...");
                    
                    long lastPlaybackPos = 0L;
                    
                    while (outputLine.isRunning()) {
                        try {
                            Thread.sleep(30);
                        } catch (InterruptedException ex) {
                            logger.finer("Interrupted while draining output line");
                        }
                        
                        firePlaybackPositionUpdate(getPlaybackPosition());
                        
                        // workaround: if the line has been started and stopped during playback, isRunning()
                        // gets stuck as true. Here we check if the playback position has stopped incrementing
                        // to detect the end of the buffer flush.
                        if (lastPlaybackPos == getPlaybackPosition()) break;
                        
                        lastPlaybackPos = getPlaybackPosition();
                    }
                    logger.finer("Finished draining output line");
                } else {
                    // this is due to a stopPlaying() -- we will preserve the output buffer
                    // in case there is a startPlaying() without an intervening seek
                    logger.finer("Stopping output line");
                    outputLine.stop();
                }

                if (reachedEOF) {
                    playing = false;
                    setPlaybackPosition(0);
                }
                
                logger.info("playback ended or paused: reachedEOF="+reachedEOF+" playing="+playing+" terminated="+terminated);
                fireStateChanged();

                for (;;) {
                    synchronized (this) {
                        if (playing || terminated) break;
                        // if not playing and not terminated, sleep again!
                    }
                    try {
                        logger.finest(String.format("Player thread sleeping for 10 seconds. playing=%b\n", playing));
                        sleep(10000);
                    } catch (InterruptedException ex) {
                        logger.finer(String.format("Player thread interrupted in sleep\n"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputLine != null) {
                outputLine.close();
                outputLine = null;
            }
        }
        logger.fine("Player thread terminated");
    }
    
    public synchronized void stopPlaying() {
        playing = false;
        // no need to interrupt in this case
    }
    
    public synchronized void startPlaying() {
        playing = true;
        interrupt();
    }
    
    public synchronized boolean isPlaying() {
        return playing;
    }
    
    /**
     * Halts playback and permanently stops this thread.
     */
    public synchronized void terminate() {
        stopPlaying();
        terminated = true;
        interrupt();
    }
    
    /**
     * Sets the current playback position of this thread. Position 0 is
     * the beginning of the clip.
     *  
     * @param sample The sample number to jump to.  The time offset this represents
     * depends on the audio format (specifically, the sampling rate) of the clip.
     */
    public synchronized void setPlaybackPosition(int sample) {
        if (in != null) {
            try {
                in.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        if (outputLine != null) {
            outputLine.stop();
            outputLine.flush();
            outputLine.start();
        }
        if (outputLine != null) {
            outputLinePositionOffset = outputLine.getLongFramePosition();
        } else {
            outputLinePositionOffset = 0L;
        }
        startSample = sample;
        in = clip.getAudio(sample);
        firePlaybackPositionUpdate(getPlaybackPosition());
    }
    
    /**
     * Returns the playback position in samples from the beginning of the clip.
     */
    public long getPlaybackPosition() {
        if (outputLine == null) {
            return 0L;
        } else {
            AudioFormat format = outputLine.getFormat();
            long elapsedSamples = (outputLine.getLongFramePosition() - outputLinePositionOffset) * format.getFrameSize();
            return elapsedSamples + startSample;
        }
    }
    
    private final List<ChangeListener> changeListeners = new ArrayList<ChangeListener>();

    /**
     * Adds a listener so it will be notified whenever this player's playback
     * state changes. Change events will happen either as a result of method
     * calls on this player ({@link #startPlaying()}, {@link #stopPlaying()},
     * and so on) or by "natural causes" such as the end of the audio stream
     * being reached.
     * <p>
     * The notifications will always be delivered on the player's own thread,
     * so change listeners must take care that their stateChanged() method
     * is thread safe.
     */
    public void addChangeListener(ChangeListener l) {
        changeListeners.add(l);
    }
    
    public void removeChangeListener(ChangeListener l) {
        changeListeners.remove(l);
    }
    
    private void fireStateChanged() {
        logger.fine("Firing state change to " + changeListeners.size() + " listeners... playing=" + playing);
        ChangeEvent e = new ChangeEvent(this);
        for (int i = changeListeners.size() - 1; i >= 0; i--) {
            changeListeners.get(i).stateChanged(e);
        }
    }
    
    private final List<PlaybackPositionListener> playbackPositionListeners = new ArrayList<PlaybackPositionListener>();

    public void addPlaybackPositionListener(PlaybackPositionListener l) {
        playbackPositionListeners.add(l);
    }

    public void removePlaybackPositionListener(PlaybackPositionListener l) {
        playbackPositionListeners.remove(l);
    }

    public void firePlaybackPositionUpdate(long samplePos) {
        logger.finest("Firing playback position update: " + samplePos);
        PlaybackPositionEvent e = new PlaybackPositionEvent(this, samplePos);
        for (int i = playbackPositionListeners.size() - 1; i >= 0; i--) {
            playbackPositionListeners.get(i).playbackPositionUpdate(e);
        }
    }
}
