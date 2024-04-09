package net.malevy.ai;

public class Message {
    private String role;
    private String content;

    public Message() {
    }

    public Message(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public String getRole() {
        return role;
    }

    public String getContent() {
        return content;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public static Message asSystem(String content) {
        return new Message("system", content);
    }

    public static Message asUser(String content) {
        return new Message("user", content);
    }
}
