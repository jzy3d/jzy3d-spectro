package org.jzy3d.spectro.trials;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

import net.bluecow.spectro.Clip;

import org.jzy3d.analysis.AbstractAnalysis;
import org.jzy3d.analysis.AnalysisLauncher;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.plot3d.primitives.axes.layout.IAxeLayout;
import org.jzy3d.plot3d.primitives.axes.layout.renderers.ElapsedTimeTickRenderer;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.view.ViewportMode;
import org.jzy3d.plot3d.rendering.view.modes.ViewPositionMode;
import org.jzy3d.spectro.primitives.FFTSurface;

/**
 * Demonstrate a spectrogram using <a href="https://code.google.com/p/spectro-edit/">Spectro Edit</a>.
 * 
 * Use either make2d(chart) or make3d(chart) in init() to switch mode
 * 
 * @author Martin Pernollet
 */
public class DemoJzy3dSpectro extends AbstractAnalysis {
    public static void main(String[] args) throws Exception {
        AnalysisLauncher.open(new DemoJzy3dSpectro());
    }

    @Override
    public void init() throws UnsupportedAudioFileException, IOException {
        File file = new File("data/sound/piano.wav");
        Clip clip = Clip.newInstance(file);

        // Create a drawable clip
        int maxFreqId = 50;
        FFTSurface surface = new FFTSurface(clip, maxFreqId);
        surface.setFaceDisplayed(true);
        surface.setWireframeDisplayed(false);

        // Create a chart with time and frequency axes
        chart = AWTChartComponentFactory.chart(Quality.Advanced, getCanvasType());
        chart.getScene().getGraph().add(surface);
        make3d(chart);
    }

    public void axeLabels(Chart chart) {
        IAxeLayout axe = chart.getAxeLayout();
        axe.setXAxeLabel("time");
        axe.setYAxeLabel("freq");
        axe.setZAxeLabel("cos");
        
        axe.setXTickRenderer(new ElapsedTimeTickRenderer());
        //axe.setYAxeLabel("note");
        //axe.setYTickProvider(new PitchTickProvider());
        //axe.setYTickRenderer(new PitchTickRenderer());
    }

    public void make2d(Chart chart) {
        chart.getView().setViewPositionMode(ViewPositionMode.TOP);
        chart.getView().getCamera().setViewportMode(ViewportMode.STRETCH_TO_FILL);
        axeLabels(chart);
    }

    public void make3d(Chart chart) {
        chart.getView().getCamera().setViewportMode(ViewportMode.STRETCH_TO_FILL);
        axeLabels(chart);
    }
}