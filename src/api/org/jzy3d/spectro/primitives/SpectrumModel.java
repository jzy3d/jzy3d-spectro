package org.jzy3d.spectro.primitives;

/** TODO : implementation should wrap spectro-edit clip
 */
public interface SpectrumModel {
    public int getFrameCount();
    public int getFrameWidth();
    public double getEnergy(int frameId, int frequencyId);
}
