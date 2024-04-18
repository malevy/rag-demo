package net.malevy;

import net.malevy.ai.Embedding;
import net.malevy.faqs.FaqRepository;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tagbio.umap.Umap;

import javax.swing.*;
import java.util.List;

@Component
public class GraphEmbeddings {
//    https://github.com/knowm/XChart
//    https://github.com/tag-bio/umap-java

    private final static Logger LOGGER = LoggerFactory.getLogger(GraphEmbeddings.class);
    private final FaqRepository faqRepository;

    public GraphEmbeddings(FaqRepository faqRepository) {
        this.faqRepository = faqRepository;
    }

    public void run() {

        final List<Embedding> embeddings = faqRepository.getEmbeddings();
        LOGGER.debug("fetched {} embeddings", embeddings.size());
        final float[][] data = new float[embeddings.size()][];
        for (int i = 0; i < embeddings.size(); i++) {
            data[i] = embeddings.get(i).vector;
        }
        final Umap umap = new Umap();
        umap.setNumberComponents(2);
        umap.setNumberNearestNeighbours(12);
        umap.setThreads(2);
        float[][] reducedEmbeddings = umap.fitTransform(data);

        float[] xdata = new float[reducedEmbeddings.length];
        float[] ydata = new float[reducedEmbeddings.length];

        for (int i = 0; i < reducedEmbeddings.length; i++) {
            xdata[i] = reducedEmbeddings[i][0];
            ydata[i] = reducedEmbeddings[i][1];
        }

        XYChart chart = new XYChartBuilder()
                .width(800)
                .height(600)
                .title("Embeddings")
                .build();

        chart.getStyler()
                .setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter)
                .setMarkerSize(10);

        chart.addSeries("All Embeddings", xdata, ydata);
        JFrame frame = new SwingWrapper<>(chart).displayChart();
    }
}
