package org.jzy3d.spectro.primitives;

import org.apache.log4j.Logger;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Range;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.Geometry;

/**
 * A drawable spectrum.
 * 
 * TODO : initialize with max frequency value instead of ID in FFT array.
 * 
 * @author Martin Pernollet
 */
public class SpectrumSurface extends Geometry {
  /**
   * The total frequency range covered by a frame always matches the frequency range of the input
   * samples, which according to the Nyquist theorem is 1/2 the sampling rate (so for a 44100hz wav
   * file, that's 0-22050hz).
   */
  public static double maxFreqOfSpectrum = 22050;

  /**
   * actual range of interest in frame spectrum. Spectrum might have 1024 dimension, but only 50
   * interesting in .
   */
  protected int maxFreqId;
  protected double maxFreqValue;

  protected int frameWidth;
  protected int frameCount;
  protected boolean absoluteZ = true;

  protected SpectrumModel spectrum;
  protected Range rangeX;
  protected Range rangeY;
  protected Range rangeZ;

  public SpectrumSurface(SpectrumModel spectrum) {
    this(spectrum, Integer.MAX_VALUE);
  }

  public SpectrumSurface(SpectrumModel spectrum, int maxFreqId) {
    // spectrum data
    this.spectrum = spectrum;
    this.maxFreqId = maxFreqId;
    this.frameCount = spectrum.getFrameCount();
    this.frameWidth = Math.min(spectrum.getFrameWidth(), maxFreqId);
    this.maxFreqValue = idToFreq(frameWidth, frameWidth);

    // bounding box
    rangeX = new Range(0, frameCount);
    rangeY = new Range(0, (float) maxFreqValue);
    rangeZ = processSpectralValuesRange(spectrum);
    if (absoluteZ)
      rangeZ.setMin(0);
    bbox = new BoundingBox3d(rangeX, rangeY, rangeZ);

    // coloring policy
    mapper = new ColorMapper(new ColorMapRainbow(), rangeZ);

    Logger.getLogger(SpectrumSurface.class).info(frameCount + " frames of " + frameWidth
        + " dims assuming max frequency " + maxFreqOfSpectrum);
  }

  @Override
  protected void begin(IPainter painter) {
    painter.glBegin_Polygon();
  }

  /** drawing polygons from spectrum values directly */
  @Override
  public void callPointsForFace(IPainter painter) {
    for (int framId = 0; framId < frameCount - 1; framId++) {
      int frameMax = Math.min(spectrum.getFrameWidth() - 1, frameWidth);
      for (int freqId = 0; freqId < frameMax; freqId++) {
        drawPolygon(painter, spectrum, framId, freqId);
      }
    }
  }

  protected void drawPolygon(IPainter painter, SpectrumModel spectrum, int frameId, int freqId) {
    float x0 = frameId;
    float x1 = frameId + 1;
    float y0 = adaptY(freqId);
    float y1 = adaptY(freqId + 1);
    float zX0Y0 = adaptZ(spectrum.getEnergy(frameId, freqId));
    float zX1Y0 = adaptZ(spectrum.getEnergy(frameId + 1, freqId));
    float zX1Y1 = adaptZ(spectrum.getEnergy(frameId + 1, freqId + 1));
    float zX0Y1 = adaptZ(spectrum.getEnergy(frameId, freqId + 1));

    callPolygonPoints(painter, x0, x1, y0, y1, zX0Y0, zX1Y0, zX1Y1, zX0Y1);
  }

  protected void callPolygonPoints(IPainter painter, float x0, float x1, float y0, float y1,
      float zX0Y0, float zX1Y0, float zX1Y1, float zX0Y1) {
    begin(painter);
    if (mapper != null) {
      painter.color(mapper.getColor(zX0Y0));
    } else {
      painter.color(Color.BLACK);
    }

    painter.glVertex3f(x0, y0, zX0Y0);
    painter.glVertex3f(x1, y0, zX1Y0);
    painter.glVertex3f(x1, y1, zX1Y1);
    painter.glVertex3f(x0, y1, zX0Y1);
    painter.glEnd();
  }

  protected float adaptY(int value) {
    return (float) idToFreq(value, frameWidth);
  }

  protected float adaptZ(double value) {
    if (absoluteZ)
      return (float) Math.abs(value);
    else
      return (float) value;
  }

  /** no wireframe */
  @Override
  public void callPointForWireframe(IPainter painter) {}

  /* UTILS */

  public double idToFreq(int i, int n) {
    double r = i / (double) n;
    return r * maxFreqOfSpectrum;
    // log2(r*maxFreq);
  }

  public double log2(double x) {
    return Math.log(x) / Math.log(2);
  }

  protected Range processSpectralValuesRange(SpectrumModel spectrum) {
    float min = Float.MAX_VALUE;
    float max = Float.MIN_VALUE;

    for (int framId = 0; framId < frameCount; framId++) {
      // Frame frame = clip.getFrame(framId);

      for (int freqId = 0; freqId < frameWidth; freqId++) {
        float value = (float) spectrum.getEnergy(framId, freqId);

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
