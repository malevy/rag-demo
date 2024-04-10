package net.malevy.ai.openai;

import net.malevy.ai.Embedding;

import java.util.List;

public class EmbeddingResponse {
    public List<Embedding> data;
    public Usage usage;

    @Override
    public String toString() {
        return "EmbeddingResponse{" +
                "data=" + data +
                ", usage=" + usage +
                '}';
    }

    static class EmbeddingWrapper {
        public List<Embedding> embedding;
        public String object;
        public int index;

        @Override
        public String toString() {
            return "EmbeddingWrapper{" +
                    "embedding=" + embedding +
                    ", object='" + object + '\'' +
                    ", index=" + index +
                    '}';
        }
    }
}
