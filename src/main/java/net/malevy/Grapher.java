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
import java.util.Arrays;
import java.util.List;

@Component
public class Grapher {
//    https://github.com/knowm/XChart
//    https://github.com/tag-bio/umap-java

    private final static Logger LOGGER = LoggerFactory.getLogger(Grapher.class);
    private final FaqRepository faqRepository;
    private final AIGateway aiGateway;

    public Grapher(FaqRepository faqRepository, AIGateway aiGateway) {
        this.faqRepository = faqRepository;
        this.aiGateway = aiGateway;
    }

    public void generate(String input) {

        final List<Embedding> faqEmbeddings = faqRepository.getEmbeddings();
        final float[][] data = new float[faqEmbeddings.size()+1][];
        for (int i = 0; i < faqEmbeddings.size(); i++) {
            data[i] = faqEmbeddings.get(i).vector;
        }

        // tack the embedding for the question to the end. all of the embeddings
        // have to be reduced as a group.
        Embedding inputEmbedding = this.aiGateway.getEmbeddingFor(input);
        data[data.length-1] = inputEmbedding.vector;

        final float[][] reducedEmbeddings = this.reducedEmbeddings(data);

        final float[] allX = new float[reducedEmbeddings.length-1];
        final float[] allY = new float[reducedEmbeddings.length-1];

        for(int i=0;i<allX.length;i++) {
            allX[i] = reducedEmbeddings[i][0];
            allY[i] = reducedEmbeddings[i][1];
        }

        final float[] inputX = new float[]{reducedEmbeddings[reducedEmbeddings.length-1][0]};
        final float[] inputY = new float[]{reducedEmbeddings[reducedEmbeddings.length-1][1]};

        XYChart chart = new XYChartBuilder()
                .width(800)
                .height(600)
                .title("Embeddings")
                .build();

        chart.getStyler()
                .setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter)
                .setMarkerSize(10);

        chart.addSeries("All Embeddings", allX, allY);
        chart.addSeries("input", inputX, inputY);
        JFrame frame = new SwingWrapper<>(chart).displayChart();
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
