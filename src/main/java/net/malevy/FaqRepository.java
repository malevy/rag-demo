package net.malevy;

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

        return this.jdbcTemplate.query(sql, new FaqMapper(), page*pagesize, pagesize);
    }

    public List<Faq> findSimilar(Embedding embedding, int cap) {
        /**
         * the article https://www.pinecone.io/learn/vector-similarity/ recommends using the same
         * vector similarity when comparing vectors as was used to train the model. The technical
         * report for the Nomic Embeded model (https://static.nomic.ai/reports/2024_Nomic_Embed_Text_Technical_Report.pdf)
         * seems to indicate that Cosine Similarity was used during the model's training so I
         * use it here
         */

        final String sql = "SELECT id, category, question, answer\n" +
                "FROM tefaqs\n" +
                "ORDER BY embedding <=> CAST(? AS vector)\n" +
                "LIMIT ?";

        Object[] parms = new Object[] {embedding.toString(), cap};
        return this.jdbcTemplate.query(sql, new FaqMapper(), parms);
    }

    public void writeEmbedding(Faq faq, Embedding embedding) {
        final String sql = "UPDATE tefaqs SET embedding = ? WHERE id = ?;";
        this.jdbcTemplate.update(sql, embedding.embedding, faq.id);
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
