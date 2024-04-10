package net.malevy.ai.openai;

import net.malevy.ai.Message;

class ChatResponse {
    public String model;
    public Choice[] choices;
    public String created_at;
    public boolean done;

    class Choice {
        public String index;
        public Message message;
        public String finish_reason;
    }
}
