package net.malevy.ai;

import java.util.Arrays;

public class Embedding {
    public double[] embedding;

    public String type = "vector";

    @Override
    public String toString() {
        return Arrays.toString(embedding).replace(" ","");
    }
}
