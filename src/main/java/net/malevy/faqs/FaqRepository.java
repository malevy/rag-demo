package net.malevy.faqs;

import net.malevy.ai.Embedding;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class FaqRepository {

    private final JdbcTemplate jdbcTemplate;

    public FaqRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<Faq> getFaqs(int page, int pagesize) {
        final String sql =
                "SELECT id, category, question, answer\n" +
                        "FROM tefaqs\n" +
                        "ORDER BY id\n" +
                        "OFFSET ? LIMIT ?;\n";

        return this.jdbcTemplate.query(sql, new FaqMapper(), page * pagesize, pagesize);
    }

    public List<Chunk> findSimilar(Embedding embedding, int cap, double threshold) {
        /**
         * the article https://www.pinecone.io/learn/vector-similarity/ recommends using the same
         * vector similarity when comparing vectors as was used to train the model. The technical
         * report for the Nomic Embeded model (https://static.nomic.ai/reports/2024_Nomic_Embed_Text_Technical_Report.pdf)
         * seems to indicate that Cosine Similarity was used during the model's training so I
         * use it here
         *
         * According to the PGVector documentation, the cosine similarity is calculated
         * as 1 - (cosine distance)
         */

        final String sql =
                "SELECT content, cos as score\n" +
                        "FROM (\n" +
                        "\tSELECT content, 1-(embedding <=> CAST(? AS vector)) AS cos\n" +
                        "\tFROM faq_embeddings\n" +
                        "\t) AS e\n" +
                        "WHERE cos > ?\n" +
                        "ORDER BY cos DESC\n" +
                        "LIMIT ?;";

        Object[] parms = new Object[]{embedding.toString(), threshold, cap};
        RowMapper<Chunk> mapper = (rs, rowNum) -> {
            return new Chunk(
                    rs.getString("content"),
                    rs.getDouble("score")
            );
        };
        return this.jdbcTemplate.query(sql, mapper, parms);
    }

    public void writeEmbedding(Faq faq, String content, Embedding embedding) {
        final String sql = "INSERT INTO faq_embeddings (faq_id, content, embedding)\n" +
                "VALUES (?, ?, ?)\n" +
                "ON CONFLICT (faq_id) DO UPDATE SET content = EXCLUDED.content, embedding = EXCLUDED.embedding;\n";
        this.jdbcTemplate.update(sql, faq.id, content, embedding.embedding);
    }

    static class FaqMapper implements RowMapper<Faq> {
        @Override
        public Faq mapRow(ResultSet resultSet, int i) throws SQLException {
            return new Faq(
                    resultSet.getInt("id"),
                    resultSet.getString("category"),
                    resultSet.getString("question"),
                    resultSet.getString("answer")
            );
        }
    }

}
