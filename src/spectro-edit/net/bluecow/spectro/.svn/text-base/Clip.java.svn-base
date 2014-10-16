/*
 * Created on Jul 8, 2008
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
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoableEditSupport;

/**
 * A Clip represents an audio clip of some length. The clip is split up
 * into a series of equal-size frames of spectral information.  The frames
 * of spectral information can be accessed in random order, and the clip
 * can also provide an AudioInputStream of the current spectral information
 * for playback or saving to a traditional PCM (WAV or AIFF) audio file.
 */
public class Clip {

    private static final Logger logger = Logger.getLogger(Clip.class.getName());

    /**
     * The audio format this class works with. Input audio will be converted to this
     * format automatically, and output data will always be created in this format.
     */
    private static final AudioFormat AUDIO_FORMAT = new AudioFormat(44100, 16, 1, true, true);

    private static final int DEFAULT_FRAME_SIZE = 1024;
    private static final int DEFAULT_OVERLAP = 2;
    
    private final List<Frame> frames = new ArrayList<Frame>();
    
    /**
     * Number of samples per frame. Currently must be a power of 2 (this is a requirement
     * of many DFT routines).
     */
    private final int frameSize;
    
    /**
     * The amount of overlap: this is the number of frames that will carry information
     * about the same sample. A value of 1 means no overlap; 2 means frames will overlap
     * to cover every sample twice, and so on.  More overlap means better time resolution.
     */
    private final int overlap;
    
    /**
     * The amount that the time samples are divided by before sending to the transformation,
     * and the amount they're multiplied after being transformed back.
     */
    private double spectralScale = 10000.0;

    /**
     * Stores the current edit in progress, or null if there is no edit in progress.
     */
    private ClipDataEdit currentEdit;

    private final UndoableEditSupport undoEventSupport = new UndoableEditSupport();

    private final String name;
    
    /**
     * Creates a new Clip based on the acoustical information in the given audio
     * file.
     * <p>
     * TODO: this could be time-consuming, so spectral conversion should be done
     * in a background thread.
     * 
     * @param file
     *            The audio file to read. Currently, single-channel WAV and AIFF
     *            are supported.
     * @throws UnsupportedAudioFileException
     *             If the given file can't be read because it's not of a
     *             supported type.
     * @throws IOException
     *             If the file can't be read for more basic reasons, such
     *             as nonexistence.
     */
    public static Clip newInstance(File file) throws UnsupportedAudioFileException, IOException {
        AudioFormat desiredFormat = AUDIO_FORMAT;
        BufferedInputStream in = new BufferedInputStream(AudioFileUtils.readAsMono(desiredFormat, file));
        return new Clip(file.getAbsolutePath(), in, DEFAULT_FRAME_SIZE, DEFAULT_OVERLAP);
    }
    
    /**
     * Creates a new clip from the audio data in the given input stream.
     * 
     * @param name The name of this clip. Could be the file name it was read from, or
     * something supplied by the user.
     * @param in The audio data to read. Must have the following characteristics:
     * <ul>
     *  <li>Contains bytes in the format that an AudioInputStream in the format
     *      specified by {@link #AUDIO_FORMAT}
     *  <li>Supports mark() and reset(). (This can be accomplished by wrapping
     *      your stream in a BufferedInputStream)
     * </ul>
     * @throws IOException If reading the input stream fails for any reason. 
     */
    private Clip(String name, InputStream in, int frameSize, int overlap) throws IOException {
        this.name = name;
        this.frameSize = frameSize;
        this.overlap = overlap;
        WindowFunction windowFunc = new VorbisWindowFunction(frameSize);
        byte[] buf = new byte[frameSize * 2]; // 16-bit mono samples
        int n;
        in.mark(buf.length * 2);
        while ( (n = readFully(in, buf)) != -1) {
            logger.finest("Read "+n+" bytes");
            if (n != buf.length) {
                // this should only happen at the end of the input file (last frame)
                logger.warning("Only read "+n+" of "+buf.length+" bytes at frame " + frames.size());
                
                // pad with silence or there will be audible junk at end of clip
                for (int i = n; i < buf.length; i++) {
                    buf[i] = 0;
                }
            }
            double[] samples = new double[frameSize];
            for (int i = 0; i < frameSize; i++) {
                int hi = buf[2*i];// & 0xff; // need sign extension
                int low = buf[2*i + 1] & 0xff;
                int sampVal = ( (hi << 8) | low);
                samples[i] = (sampVal / spectralScale);
            }
            
            frames.add(new Frame(samples, windowFunc));
            in.reset();
            long bytesToSkip = (frameSize * 2) / overlap;
            long bytesSkipped;
            if ( (bytesSkipped = in.skip(bytesToSkip)) != bytesToSkip) {
                logger.info("Skipped " + bytesSkipped + " bytes, but wanted " + bytesToSkip + " at frame " + frames.size());
            }
            in.mark(buf.length * 2);
        }
        
        logger.info(String.format("Read %d frames from %s (%d bytes). frameSize=%d overlap=%d\n", frames.size(), name, frames.size() * buf.length, frameSize, overlap));
    }
    
    /**
     * Fills the given buffer by reading the given input stream repeatedly
     * until the buffer is full. The only conditions that will prevent buf
     * from being filled by the time this method returns are if the input
     * stream indicates an EOF condition or an IO error occurs.
     * 
     * @param in The input stream to read
     * @param buf The buffer to fill with bytes from the input stream
     * @return The number of bytes actually read into buf
     * @throws IOException If an IO error occurs
     */
    private int readFully(InputStream in, byte[] buf) throws IOException {
        int offset = 0;
        int length = buf.length;
        int bytesRead = 0;
        while ( (offset < buf.length) && ((bytesRead = in.read(buf, offset, length)) != -1) ) {
            logger.finest("read " + bytesRead + " bytes at offset " + offset);
            length -= bytesRead;
            offset += bytesRead;
        }
        if (offset > 0) {
            logger.fine("Returning " + offset + " bytes read into buf");
            return offset;
        } else {
            logger.fine("Returning EOF");
            return -1;
        }
    }
    /**
     * Returns the number of time samples per frame.
     */
    public int getFrameTimeSamples() {
        return frameSize;
    }

    /**
     * Returns the number of frequency samples per frame.
     */
    public int getFrameFreqSamples() {
        return frameSize;
    }

    /**
     * Returns the number of frames.
     * @return
     */
    public int getFrameCount() {
        return frames.size();
    }
    
    /**
     * Returns the <i>i</i>th frame.
     * 
     * @param i The frame number--frame numbering starts with 0.
     * @return The <i>i</i>th frame. The returned frame is mutable; modifying
     * its data permanently alters the acoustic qualities of this clip.
     */
    public Frame getFrame(int i) {
        return frames.get(i);
    }

    /**
     * Returns the number of frames that overlap to produce any given time sample.
     * An overlap of at least 2 is required in order to produce a click-free result
     * after modifying the specral information. Larger values give better time
     * resolution at the cost of a linear increase in memory and CPU consumption.
     */
    public int getOverlap() {
        return overlap;
    }

    /**
     * Returns the audio data in this clip as an AudioInputStream.
     * @return
     */
    public AudioInputStream getAudio() {
        return getAudio(0);
    }
    
    public AudioInputStream getAudio(int sample) {
        return getAudio(sample, Integer.MAX_VALUE);
    }

    /**
     * Returns the time-domain audio data for some or all of this clip.
     * 
     * @param sample
     *            The starting sample position for the returned audio stream.
     * @param length
     *            The maximum number of samples to include in the audio stream.
     *            If this length argument specifies a sample position past the
     *            end of the clip, it will be ignored (extra padding will not be
     *            added).
     * @return an audio stream in the AUDIO_FORMAT format that begins with the
     *         sound at the given start sample and ends either at the natural
     *         end of this clip, or after returning <code>length</code> samples.
     */
    public AudioInputStream getAudio(int sample, int length) {
        // TODO prefill overlap buffer with previous frame's data
        // TODO calculate sample offset into the initial frame
        final int initialFrame = sample / getFrameTimeSamples();
        
        InputStream audioData = new InputStream() {

            /**
             * Next frame to decode for playback.
             */
            int nextFrame = initialFrame;
            
            /**
             * A data structure that holds all the current frames of floating point samples
             * and performs the overlap-and-combine operation for us.
             */
            OverlapBuffer overlapBuffer = new OverlapBuffer(frameSize, overlap);
            
            /**
             * The current sample data. Only the lower 16 bits are significant.
             */
            int currentSample;
            
            /**
             * Flag to indicate if the current byte being read from the input stream
             * is the high byte or the low byte of a single 16-bit sample.
             */
            boolean currentByteHigh = true;
            
            int emptyFrameCount = 0;

            @Override
            public int available() throws IOException {
                return Integer.MAX_VALUE;
            }
            
            @Override
            public int read() throws IOException {
                if (overlapBuffer.needsNewFrame()) {
                    if (nextFrame < frames.size()) {
                        Frame f = frames.get(nextFrame++);
                        overlapBuffer.addFrame(f.asTimeData());
                    } else {
                        overlapBuffer.addEmptyFrame();
                        emptyFrameCount++;
                    }
                }
                
                if (emptyFrameCount >= overlap) {
                    return -1;
                } else if (currentByteHigh) {
                    currentSample = (int) (overlapBuffer.next() * spectralScale);
                    currentByteHigh = false;
                    return (currentSample >> 8) & 0xff;
                } else {
                    currentByteHigh = true;
                    return currentSample & 0xff;
                }
                
            }
            
        };
        int clipLength = getFrameCount() * getFrameTimeSamples() * (AUDIO_FORMAT.getSampleSizeInBits() / 8) / overlap;
        return new AudioInputStream(audioData, AUDIO_FORMAT, Math.min(length, clipLength));
    }

    /**
     * Tells this clip that client code intends to modify some of the frame data
     * in the given region. The data in this region will be "backed up" for
     * purposes of revert and undo/redo.
     * <p>
     * In order to conserve undo/redo memory, it's best to keep this region as
     * small as possible (don't just say you're going to edit the whole clip
     * unless that's really true).
     * 
     * @param region
     *            The region that will be edited. The x value indicates the
     *            index of the first frame in the region, and the width
     *            indicates the number of frames. The y value indicates the
     *            first frequency index, and the height the number of frequency
     *            indexes.
     * @param description
     *            User-visible description of the edit that is going to take
     *            place.
     */
    public void beginEdit(Rectangle region, String description) {
        if (currentEdit != null) {
            throw new IllegalStateException("Already in an edit: " + currentEdit);
        }
        currentEdit = new ClipDataEdit(this, region.x, region.y, region.width, region.height);
    }

    /**
     * Ends the edit that was previously started by
     * {@link #beginEdit(Rectangle, String)}, and notifies listeners that the
     * edit region has changed. There is no need to call
     * {@link #regionChanged(Rectangle)} after this.
     */
    public void endEdit() {
        if (currentEdit == null) {
            throw new IllegalStateException("No edit is in progress");
        }
        currentEdit.captureNewData();
        undoEventSupport.postEdit(currentEdit);
        regionChanged(currentEdit.getRegion());
        currentEdit = null;
    }

    /**
     * Puts the undo system into a state where it accumulates edits that happen
     * from now on. To return to the initial state (which also posts the
     * compound edit to the undo listeners), call {@link #endCompoundEdit()}.
     * <p>
     * This "compound edit" state nests: three calls to this method requires
     * three subsequent calls to endCompoundEdit() in order to return from the
     * compound edit state.
     * 
     * @param presentationName
     *            The name or phrase to show the user for this group of edits.
     *            Should make sense as a sentence if the word "Undo" or "Redo"
     *            is prepended.
     */
    public void beginCompoundEdit(String presentationName) {
        undoEventSupport.beginUpdate(); // TODO subclass support class and grab presentation name
    }

    /**
     * Ends the compound edit in progress. It is an error to call this method more
     * times than {@link #beginCompoundEdit(String)} has already been called.
     */
    public void endCompoundEdit() {
        undoEventSupport.endUpdate();
    }

    /**
     * Notifies all registered ClipDataChangeListeners that the clip data has
     * changed for the given region. This method works independently of
     * undo/redo: it can be called whether or not an edit is in progress, and it
     * never affects the undo/redo state.
     * <p>
     * Two typical use cases are for showing interim changes during an edit (for
     * example, immediate feedback while the user is adjusting a slider) and
     * internally, for the undo system to provide notification after completing
     * an undo or redo operation.
     * 
     * @param region
     *            The region that changed. See
     *            {@link #beginEdit(Rectangle, String)} for a description of how
     *            the rectangle's geometry maps to frames and frequencies.
     */
    public void regionChanged(Rectangle region) {
        fireClipDataChangeEvent(region);
    }
    

    // --------------------- ClipDataChangeEvent crap -------------------------
    
    private final List<ClipDataChangeListener> clipDataChangeListeners =
        new ArrayList<ClipDataChangeListener>();
    
    public void addClipDataChangeListener(ClipDataChangeListener l) {
        clipDataChangeListeners.add(l);
    }

    public void removeClipDataChangeListener(ClipDataChangeListener l) {
        clipDataChangeListeners.remove(l);
    }
    
    private void fireClipDataChangeEvent(Rectangle region) {
        ClipDataChangeEvent e = new ClipDataChangeEvent(this, region);
        for (int i = clipDataChangeListeners.size() - 1; i >= 0; i--) {
            clipDataChangeListeners.get(i).clipDataChanged(e);
        }
    }

    
    // -------------------- Undo event support ---------------------
    
    public void addUndoableEditListener(UndoableEditListener l) {
        undoEventSupport.addUndoableEditListener(l);
    }

    public UndoableEditListener[] getUndoableEditListeners() {
        return undoEventSupport.getUndoableEditListeners();
    }

    public void removeUndoableEditListener(UndoableEditListener l) {
        undoEventSupport.removeUndoableEditListener(l);
    }

    public double getSamplingRate() {
        return AUDIO_FORMAT.getSampleRate();
    }

    /**
     * Creates a new Clip instance based on the given range of frames in the
     * current clip.
     * <p>
     * Currently, the audio data of the sub clip is independent of the clip it
     * was obtained from. This is not really a desirable situation (it would be
     * better if the underlying data were shared), so don't count on this
     * behaviour!
     * 
     * @param startFrame
     *            The first frame of this clip that should appear in the subclip
     * @param nFrames
     *            The number of frames of this clip that should appear in the subclip.
     * @param newFrameSize
     *            The frame size of the subclip. This determines the frequency
     *            resolution--larger frames give more vertical (frequency)
     *            resolution. Must be a power of two.
     * @param newOverlap
     *            The degree of overlap for frames in the subclip. Larger values
     *            give more horizontal (time) resolution.
     * @return
     */
    public Clip subClip(int startFrame, int nFrames, int newFrameSize, int newOverlap) {
        InputStream in = null;
        try {
            // decode existing
            in = new BufferedInputStream(getAudio(startFrame * frameSize, nFrames * frameSize));

            // create new clip with new settings
            Clip subClip = new Clip("Part of " + name, in, newFrameSize, newOverlap);
            return subClip;
        } catch (IOException ex) {
            AssertionError err = new AssertionError("Unexpected IO Exception during clip resampling");
            err.initCause(ex);
            throw err;
        } finally {
            try {
                if (in != null) in.close();
            } catch (IOException ex) {
                logger.log(Level.WARNING, "Failed to close input stream after creating subclip", ex);
            }
        }
    }

}
