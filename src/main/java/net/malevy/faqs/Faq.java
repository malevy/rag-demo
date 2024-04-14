package net.malevy.faqs;

import java.util.Objects;

public class Faq {
    final int id;
    final String category;
    final String question;
    final String answer;

    public Faq(int id, String category, String question, String answer) {
        this.id = id;
        this.category = category;
        this.question = question;
        this.answer = answer;
    }

    public int getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public String toModelFriendlyString() {
        final String template = "CATEGORY:%s\nQUESTION:%s\nANSWER:%s";
        return String.format(template,
                scrub(this.category),
                scrub(this.question),
                scrub(this.answer)
        );
    }

    private String scrub(String s) {
        // remove any CRs
        return s.replace("\r", "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Faq faq = (Faq) o;

        if (id != faq.id) return false;
        if (!Objects.equals(category, faq.category)) return false;
        if (!Objects.equals(question, faq.question)) return false;
        return Objects.equals(answer, faq.answer);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (category != null ? category.hashCode() : 0);
        result = 31 * result + (question != null ? question.hashCode() : 0);
        result = 31 * result + (answer != null ? answer.hashCode() : 0);
        return result;
    }
}
