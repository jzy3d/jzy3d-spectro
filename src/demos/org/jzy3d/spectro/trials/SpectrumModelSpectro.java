package org.jzy3d.spectro.trials;

import org.jzy3d.spectro.primitives.SpectrumModel;

import net.bluecow.spectro.Clip;

public class SpectrumModelSpectro implements SpectrumModel{
    protected Clip clip;
        
    public SpectrumModelSpectro(Clip clip) {
        this.clip = clip;
    }

    @Override
    public int getFrameCount() {
        return  clip.getFrameCount();
    }

    @Override
    public int getFrameWidth() {
        return clip.getFrame(0).getLength();
    }

    @Override
    public double getEnergy(int frameId, int frequencyId) {
        return clip.getFrame(frameId).getReal(frequencyId);
    }
}
