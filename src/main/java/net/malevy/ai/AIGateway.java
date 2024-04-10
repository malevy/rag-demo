package net.malevy.ai;

import java.util.List;

public interface AIGateway {
    Embedding getEmbeddingFor(String text);
    Message submitChat(List<Message> messages);
}
