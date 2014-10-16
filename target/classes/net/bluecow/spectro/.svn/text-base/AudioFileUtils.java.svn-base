/*
 * Created on Oct 21, 2008
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioFileUtils {

    private static final Logger logger = Logger.getLogger(AudioFileUtils.class.getName());
    
    /**
     * Reads the given file, returning a 1-channel input stream containing
     * a mix of the two stereo channels in the given file.
     * 
     * @param desiredFormat The format that the data should be in in the returned
     * AudioInputStream. The channels value must be 1, and the sample size in bits
     * must be 16.
     * @param file The file to read. Currently, WAV and AIFF are supported.
     * Future support for mp3 and ogg vorbis is planned.
     * @return an audio input stream with 1 channel
     * @throws IOException If the file can't be read
     * @throws UnsupportedAudioFileException If the audio system doesn't support the audio file's type
     * @throws IllegalArgumentException if the 
     */
    public static AudioInputStream readAsMono(final AudioFormat desiredFormat, File file) throws UnsupportedAudioFileException, IOException {
        if (desiredFormat.getSampleSizeInBits() != 16) {
            throw new UnsupportedOperationException(
                    "Only 16-bit samples are supported at the moment " +
                    "(you requested " + desiredFormat.getSampleSizeInBits() + ")");
        }
        if (desiredFormat.getChannels() != 1) {
            throw new UnsupportedOperationException("Desired number of channels should be 1 " +
            		"(you requested " + desiredFormat.getChannels() + ")");
        }
        AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(file);
        if (fileFormat.getFormat().getChannels() == 1) {
            return AudioSystem.getAudioInputStream(desiredFormat, AudioSystem.getAudioInputStream(file));
        } else if (fileFormat.getFormat().getChannels() == 2) {
            AudioFormat stereoDesiredFormat = new AudioFormat(
                    desiredFormat.getEncoding(),
                    desiredFormat.getSampleRate(),
                    16,
                    2,
                    4,
                    desiredFormat.getFrameRate(),
                    desiredFormat.isBigEndian(),
                    desiredFormat.properties());
            final AudioInputStream stereoIn = AudioSystem.getAudioInputStream(
                    stereoDesiredFormat, AudioSystem.getAudioInputStream(file));
            InputStream mixed = new InputStream() {

                byte[] monobuf = new byte[16384];
                int offset = 0;
                int length = 0;
                
                long bytesRead = 0;
                
                @Override
                public int read() throws IOException {
                    if (offset < length) {
                        bytesRead++;
                        return monobuf[offset++] & 0xff;
                    }
                    length = stereoIn.read(monobuf);
                    if (length <= 0) {
                        logger.fine("reached EOF on original input stream (read " + length + " bytes)");
                        return -1;
                    }
                    // monobuf now contains stereo samples. let's mix them down.
                    for (int i = 0; i < length; i += 4) {
                        int lh, ll, rh, rl;
                        if (desiredFormat.isBigEndian()) {
                            lh = monobuf[i + 0];
                            ll = monobuf[i + 1] & 0xff;
                            rh = monobuf[i + 2];
                            rl = monobuf[i + 3] & 0xff;
                        } else {
                            lh = monobuf[i + 1];
                            ll = monobuf[i + 0] & 0xff;
                            rh = monobuf[i + 3];
                            rl = monobuf[i + 2] & 0xff;
                        }
                        int left = (lh << 8 | ll);
                        int right = (rh << 8 | rl);
                        int mixed = (left + right) / 2;
                        if (desiredFormat.isBigEndian()) {
                            monobuf[(i/2) + 1] = (byte) (mixed & 0xff);
                            monobuf[(i/2) + 0] = (byte) ((mixed >> 8) & 0xff);
                        } else {
                            monobuf[(i/2) + 0] = (byte) (mixed & 0xff);
                            monobuf[(i/2) + 1] = (byte) ((mixed >> 8) & 0xff);
                        }
                    }
                    length /= 2;
                    offset = 0;
                    return monobuf[offset++] & 0xff;
                }
                
                @Override
                public synchronized void mark(int readlimit) {
                    throw new UnsupportedOperationException("Mark not supported");
                }
            };
            logger.info("Creating 1-channel mixed input stream from stereo source");
            return new AudioInputStream(mixed, desiredFormat, stereoIn.getFrameLength());
        } else {
            throw new UnsupportedAudioFileException(
                    "Unsupported number of channels: " + fileFormat.getFormat().getChannels());
        }
    }
}
