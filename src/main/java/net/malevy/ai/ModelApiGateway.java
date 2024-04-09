package net.malevy.ai;

import net.malevy.faqs.Faq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;


@Service
public class ModelApiGateway {

    final static Logger LOGGER = LoggerFactory.getLogger(ModelApiGateway.class.getName());

    final RestTemplate restTemplate = new RestTemplate();
    private final ModelApiSettings settings;

    public ModelApiGateway(ModelApiSettings settings) {
        this.settings = settings;
    }

    public Embedding getEmbeddingFor(Faq faq) {
        Objects.requireNonNull(faq, "must provide an Faq instance");
        LOGGER.debug(String.format("creating embedding for FAQ %d", faq.getId()));
        return getEmbeddingFor(faq.toModelFriendlyString());

    }

    public Embedding getEmbeddingFor(String str) {
        Objects.requireNonNull(str, "must provide an Faq instance");
        if (str.isBlank()) throw new IllegalArgumentException("cannot process an empty input");

        EmbeddingRequest payload = new EmbeddingRequest(this.settings.getEmbeddingModel(), str);
        Embedding embedding = this.restTemplate.postForObject(this.settings.getEmbeddingUri(), payload, Embedding.class);

        return embedding;
    }

    public Message submitChat(List<Message> messages) {
        Objects.requireNonNull(messages, "must provide messages");
        if (messages.isEmpty()) throw new IllegalArgumentException("must provide messages");

        ChatRequest request = new ChatRequest(this.settings.getChatModel(), messages);
        ResponseEntity<ChatResponse> response = this.restTemplate.postForEntity(this.settings.getChatUri(), request, ChatResponse.class);
        return response.getBody().message;
    }


    static class EmbeddingRequest {

        final private String model;
        private String prompt;

        public EmbeddingRequest(String model, String prompt) {
            this.model = model;
            this.prompt = prompt;
        }

        public String getModel() {
            return model;
        }

        public String getPrompt() {
            return prompt;
        }

        public void setPrompt(String prompt) {
            this.prompt = prompt;
        }
    }

    static class ChatRequest {
        private String model;
        private List<Message> messages;

        public final boolean stream = false;
        public final String format = "json";

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
    }

    public static class ChatResponse {
        public String model;
        public Message message;
        public String created_at;
        public boolean done;
    }

}