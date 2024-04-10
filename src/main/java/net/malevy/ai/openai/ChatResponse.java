package net.malevy.ai.openai;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.malevy.ai.Message;

import java.util.Arrays;

class ChatResponse {
    public String model;
    public Choice[] choices;
    public String created;
    public Usage usage;

    @Override
    public String toString() {
        return "ChatResponse{" +
                "model='" + model + '\'' +
                ", choices=" + Arrays.toString(choices) +
                ", created='" + created + '\'' +
                ", usage=" + usage +
                '}';
    }

    public static class Choice {
        public String index;
        public Message message;
        public String finish_reason;

        @Override
        public String toString() {
            return "Choice{" +
                    "index='" + index + '\'' +
                    ", message=" + message +
                    ", finish_reason='" + finish_reason + '\'' +
                    '}';
        }
    }
}
