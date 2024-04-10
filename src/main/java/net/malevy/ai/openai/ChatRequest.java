package net.malevy.ai.openai;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.malevy.ai.Message;

import java.util.List;

class ChatRequest {
    private String model;
    private List<Message> messages;

    public ChatRequest(String model, List<Message> messages) {
        this.model = model;
        this.messages = messages;
    }

    public String getModel() {
        return model;
    }

    public List<Message> getMessages() {
        return messages;
    }

    @Override
    public String toString() {
        return "ChatRequest{" +
                "model='" + model + '\'' +
                ", messages=" + messages +
                '}';
    }
}
