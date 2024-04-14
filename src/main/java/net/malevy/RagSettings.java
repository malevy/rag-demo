package net.malevy;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "rag")
public class RagSettings {

    private ChatSettings chat;
    private GeneratorSettings generator;

    public ChatSettings getChat() {
        return chat;
    }

    public void setChat(ChatSettings chat) {
        this.chat = chat;
    }

    public GeneratorSettings getGenerator() {
        return generator;
    }

    public void setGenerator(GeneratorSettings generator) {
        this.generator = generator;
    }

    public static class ChatSettings {
        private int topk;
        private int expansionCount;
        private double similarityThreshold;

        public int getTopk() {
            return topk;
        }

        public void setTopk(int topk) {
            this.topk = topk;
        }

        public int getExpansionCount() {
            return expansionCount;
        }

        public void setExpansionCount(int expansionCount) {
            this.expansionCount = expansionCount;
        }

        public double getSimilarityThreshold() {
            return similarityThreshold;
        }

        public void setSimilarityThreshold(double similarityThreshold) {
            this.similarityThreshold = similarityThreshold;
        }
    }

    public static class GeneratorSettings {
        private int batchSize;

        public int getBatchSize() {
            return batchSize;
        }

        public void setBatchSize(int batchSize) {
            this.batchSize = batchSize;
        }
    }
}
