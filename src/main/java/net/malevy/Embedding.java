package net.malevy;

import java.util.Arrays;

class Embedding {
    public double[] embedding;

    public String type = "vector";

    @Override
    public String toString() {
        return Arrays.toString(embedding).replace(" ","");
    }
}
