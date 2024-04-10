package net.malevy.ai.openai;

import net.malevy.ai.Embedding;

import java.util.List;

public class EmbeddingResponse {
    public List<Embedding> data;
    public Usage usage;

    static class EmbeddingWrapper {
        public List<Embedding> embedding;
        public String object;
        public int index;

    }
}
