package net.malevy.faqs;

import java.util.Objects;

public class Chunk implements Comparable<Chunk> {
    public String content;
    public double score;

    public Chunk(String content, double score) {
        this.content = content;
        this.score = score;
    }

    @Override
    public int compareTo(Chunk other) {
        return Double.compare(other.score, this.score);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Chunk chunk = (Chunk) o;

        return Objects.equals(content, chunk.content);
    }

    @Override
    public int hashCode() {
        return content != null ? content.hashCode() : 0;
    }
}
