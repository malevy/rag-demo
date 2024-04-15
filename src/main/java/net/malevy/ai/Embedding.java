package net.malevy.ai;

import java.util.Arrays;

public class Embedding {

    public double[] embedding;

    @Override
    public String toString() {
        return Arrays.toString(embedding).replace(" ","");
    }

    /**
     * Determines if two embeddings are equal. They are if they have the same value in the same order.
     * @param o - the other embedding
     * @return true if they are equal; false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Embedding embedding1 = (Embedding) o;
        return Arrays.equals(embedding, embedding1.embedding);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(embedding);
    }
}
