package org.jzy3d.spectro.trials;

import net.bluecow.spectro.Clip;

import org.jzy3d.spectro.primitives.SpectrumModel;

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
    
    public double getFrequencyAt(int index){
        return  index * sampleRate / getFrameWidth();
    }
    
    public double[] getFrequencies(){
        double[] frequencies = new double[getFrameWidth()];
        for (int i = 0; i < frequencies.length; i++) {
            frequencies[i] = getFrequencyAt(i);
        }
        return frequencies;
    }
    
    public double sampleRate = 44100;

    @Override
    public double getEnergy(int frameId, int frequencyId) {
        return clip.getFrame(frameId).getReal(frequencyId);
    }
}
