package net.malevy;

import net.malevy.ai.AIGateway;
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
public class Grapher {
//    https://github.com/knowm/XChart
//    https://github.com/tag-bio/umap-java

    private final static Logger LOGGER = LoggerFactory.getLogger(Grapher.class);
    private final FaqRepository faqRepository;
    private final AIGateway aiGateway;

    private float[] generalEmbedX = null;
    private float[] generalEmbedY = null;

    public Grapher(FaqRepository faqRepository, AIGateway aiGateway) {
        this.faqRepository = faqRepository;
        this.aiGateway = aiGateway;
    }

    public void generate(String input) {

        if (generalEmbedX == null) {
            processGeneralEmbeddings();
        }

        Embedding embedding = this.aiGateway.getEmbeddingFor(input);
        float[][] vector = new float[1][];
        vector[0] = embedding.vector;
        float[][] reducedInputEmbedding = this.reducedEmbeddings(vector);

        XYChart chart = new XYChartBuilder()
                .width(800)
                .height(600)
                .title("Embeddings")
                .build();

        chart.getStyler()
                .setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter)
                .setMarkerSize(10);

        chart.addSeries("All Embeddings", this.generalEmbedX, this.generalEmbedY);
        float[] xSeries = new float[]{reducedInputEmbedding[0][0]};
        float[] ySeries = new float[]{reducedInputEmbedding[0][1]};
        chart.addSeries("input", xSeries, ySeries);
        JFrame frame = new SwingWrapper<>(chart).displayChart();
    }

    private void processGeneralEmbeddings() {

        LOGGER.info("first time processing of all embeddings");

        final List<Embedding> embeddings = faqRepository.getEmbeddings();
        LOGGER.debug("fetched {} embeddings", embeddings.size());
        final float[][] data = new float[embeddings.size()][];
        for (int i = 0; i < embeddings.size(); i++) {
            data[i] = embeddings.get(i).vector;
        }
        float[][] reducedEmbeddings = reducedEmbeddings(data);

        this.generalEmbedX = new float[reducedEmbeddings.length];
        this.generalEmbedY = new float[reducedEmbeddings.length];

        for (int i = 0; i < reducedEmbeddings.length; i++) {
            this.generalEmbedX[i] = reducedEmbeddings[i][0];
            this.generalEmbedY[i] = reducedEmbeddings[i][1];
        }

    }

    private float[][] reducedEmbeddings(float[][] data) {
        final Umap umap = new Umap();
        umap.setNumberComponents(2);
        umap.setNumberNearestNeighbours(12);
        umap.setThreads(2);
        float[][] reducedEmbeddings = umap.fitTransform(data);
        return reducedEmbeddings;
    }
}
