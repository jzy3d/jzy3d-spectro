package org.jzy3d.spectro.primitives;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import net.bluecow.spectro.Clip;
import net.bluecow.spectro.Frame;

import org.apache.log4j.Logger;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.primitives.AbstractGeometry;
import org.jzy3d.plot3d.rendering.compat.GLES2CompatUtils;

/**
 * A drawable spectrum.
 * 
 * TODO : initialize with max frequency value instead of ID in FFT array.
 * 
 * @author Martin Pernollet
 */
public class FFTSurface extends AbstractGeometry {
    /**
     * The total frequency range covered by a frame always matches the frequency
     * range of the input samples, which according to the Nyquist theorem is 1/2
     * the sampling rate (so for a 44100hz wav file, that's 0-22050hz).
     */
    public static double maxFreqOfSpectrum = 22050;

    /**
     * actual range of interest in frame spectrum. Spectrum might have 1024
     * dimension, but only 50 interesting in .
     */
    protected int maxFreqId;
    protected double maxFreqValue;

    protected int frameDimensions;
    protected int frameCount;
    protected boolean absoluteZ = true;

    protected Clip clip;
    protected Range range;

    public FFTSurface(Clip clip) {
        this(clip, Integer.MAX_VALUE);
    }

    public FFTSurface(Clip clip, int maxFreqId) {
        this.clip = clip;
        this.maxFreqId = maxFreqId;
        this.frameCount = clip.getFrameCount();
        this.frameDimensions = Math.min(clip.getFrame(0).getLength(), maxFreqId);
        this.maxFreqValue = idToFreq(frameDimensions, frameDimensions);

        Logger.getLogger(FFTSurface.class).info(frameCount + " frames of " + frameDimensions + " dims assuming max frequency " + maxFreqOfSpectrum);

        range = processSpectralValuesRange(clip);
        if (absoluteZ)
            range.setMin(0);
        mapper = new ColorMapper(new ColorMapRainbow(), range);
        bbox = new BoundingBox3d(0f, (float) frameCount, 0f, (float) maxFreqValue, (float) range.getMin(), (float) range.getMax());
    }

    @Override
    protected void begin(GL gl) {
        if (gl.isGL2()) {
            gl.getGL2().glBegin(geometry());
        } else {
            GLES2CompatUtils.glBegin(geometry());
        }
    }

    protected int geometry() {
        return GL2.GL_POLYGON;
    }

    /** drawing polygons from spectrum values directly */
    public void callPointsForFaceGL2(GL gl) {
        for (int framId = 0; framId < frameCount - 1; framId++) {
            Frame frame = clip.getFrame(framId);
            Frame frameNext = clip.getFrame(framId + 1);
            int frameMax = Math.min(frame.getLength() - 1, frameDimensions);
            for (int freqId = 0; freqId < frameMax; freqId++) {
                drawPolygon(gl, framId, frame, frameNext, freqId);
            }
        }
    }

    protected void drawPolygon(GL gl, int framId, Frame frame, Frame frameNext, int freqId) {
        double energyValueX0Y0 = adaptZ(frame.getReal(freqId));
        double energyValueX1Y0 = adaptZ(frameNext.getReal(freqId));
        double energyValueX1Y1 = adaptZ(frameNext.getReal(freqId + 1));
        double energyValueX0Y1 = adaptZ(frame.getReal(freqId + 1));

        float x0 = (float) framId;
        float x1 = (float) framId + 1;
        float y0 = (float) adaptY(freqId);
        float y1 = (float) adaptY(freqId + 1);

        callPolygonPoints(gl, energyValueX0Y0, energyValueX1Y0, energyValueX1Y1, energyValueX0Y1, x0, x1, y0, y1);
    }

    protected void callPolygonPoints(GL gl, double energyValueX0Y0, double energyValueX1Y0, double energyValueX1Y1, double energyValueX0Y1, float x0, float x1, float y0, float y1) {
        begin(gl);
        if (mapper != null) {
            call(gl, mapper.getColor(energyValueX0Y0));
        } else {
            call(gl, Color.BLACK);
        }

        GL2 gl2 = gl.getGL2();
        gl2.glVertex3f(x0, y0, (float) energyValueX0Y0);
        gl2.glVertex3f(x1, y0, (float) energyValueX1Y0);
        gl2.glVertex3f(x1, y1, (float) energyValueX1Y1);
        gl2.glVertex3f(x0, y1, (float) energyValueX0Y1);
        end(gl);
    }

    protected double adaptY(int value) {
        return idToFreq(value, frameDimensions);
    }

    protected double adaptZ(double value) {
        if (absoluteZ)
            return Math.abs(value);
        else
            return value;
    }

    /** no wireframe */
    public void callPointsForWireframeGL2(GL gl) {
    }

    /* UTILS */

    public double idToFreq(int i, int n) {
        double r = i / (double) n;
        return r * maxFreqOfSpectrum;
        // log2(r*maxFreq);
    }

    public double log2(double x) {
        return Math.log(x) / Math.log(2);
    }

    protected Range processSpectralValuesRange(Clip clip) {
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;

        for (int framId = 0; framId < frameCount; framId++) {
            Frame frame = clip.getFrame(framId);

            for (int freqId = 0; freqId < frameDimensions; freqId++) {
                double value = frame.getReal(freqId);

                if (min > value) {
                    min = value;
                }
                if (max < value) {
                    max = value;
                }
            }
        }
        Range range = new Range(min, max);
        return range;
    }
}
