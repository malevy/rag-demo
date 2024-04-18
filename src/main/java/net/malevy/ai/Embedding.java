package net.malevy.ai;

import org.postgresql.util.PGobject;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Arrays;

public class Embedding extends PGobject implements Serializable, Cloneable {

    public float[] vector;

    public Embedding() {
        type = "vector"; // the PSQL object type
    }

    public Embedding(String str) throws SQLException {
        this();
        this.setValue(str);
    }

    @Override
    public String toString() {
        if (vector == null) {
            return null;
        }
        return Arrays.toString(vector).replace(" ","");
    }

    @Override
    public void setValue(String value) throws SQLException {
        if (value == null) {
            vector = null;
            return;
        }

        final String[] values = value.substring(1, value.length() - 1).split(",");
        vector = new float[values.length];
        for (int i = 0; i < values.length; i++) {
            vector[i] = Float.parseFloat(values[i]);
        }
    }

    @Override
    public String getValue() {
        return this.toString();
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
        return Arrays.equals(vector, embedding1.vector);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(vector);
    }
}
