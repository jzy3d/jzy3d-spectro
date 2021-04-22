package org.jzy3d.spectro.trials;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.jzy3d.analysis.AWTAbstractAnalysis;
import org.jzy3d.analysis.AnalysisLauncher;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.plot3d.primitives.axis.layout.IAxisLayout;
import org.jzy3d.plot3d.primitives.axis.layout.renderers.ElapsedTimeTickRenderer;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.view.ViewportMode;
import org.jzy3d.plot3d.rendering.view.modes.ViewPositionMode;
import org.jzy3d.spectro.primitives.SpectrumSurface;
import net.bluecow.spectro.Clip;

/**
 * Demonstrate a spectrogram using <a href="https://code.google.com/p/spectro-edit/">Spectro
 * Edit</a>.
 * 
 * Use either make2d(chart) or make3d(chart) in init() to switch mode
 * 
 * @author Martin Pernollet
 */
public class DemoJzy3dSpectro extends AWTAbstractAnalysis {
  public static void main(String[] args) throws Exception {
    AnalysisLauncher.open(new DemoJzy3dSpectro());
  }

  @Override
  public void init() throws UnsupportedAudioFileException, IOException {
    File file = new File("data/sound/piano.wav");
    Clip clip = Clip.newInstance(file);

    // Create a drawable clip
    int maxFreqId = 50;
    SpectrumSurface surface = new SpectrumSurface(new SpectrumModelSpectro(clip), maxFreqId);
    surface.setFaceDisplayed(true);
    surface.setWireframeDisplayed(false);

    // Create a chart with time and frequency axes
    chart = AWTChartFactory.chart(Quality.Advanced());
    chart.getScene().getGraph().add(surface);
    // make2d(chart);
  }

  public void axeLabels(Chart chart) {
    IAxisLayout axe = chart.getAxisLayout();
    axe.setXAxisLabel("time");
    axe.setYAxisLabel("freq");
    axe.setZAxisLabel("cos");

    axe.setXTickRenderer(new ElapsedTimeTickRenderer());
    // axe.setYAxeLabel("note");
    // axe.setYTickProvider(new PitchTickProvider());
    // axe.setYTickRenderer(new PitchTickRenderer());
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
