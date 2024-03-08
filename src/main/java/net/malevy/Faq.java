package net.malevy;

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

}
