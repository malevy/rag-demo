package net.malevy.ai.openai;

import net.malevy.ai.AIGateway;
import net.malevy.ai.Embedding;
import net.malevy.ai.Message;
import net.malevy.ai.ModelApiSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;


@Profile("openai")
@Service
public class OpenAIApiGateway implements AIGateway {

    final static Logger LOGGER = LoggerFactory.getLogger(OpenAIApiGateway.class.getName());

    final RestTemplate restTemplate = new RestTemplate();
    private final ModelApiSettings settings;

    public OpenAIApiGateway(ModelApiSettings settings) {
        this.settings = settings;
    }


    public Embedding getEmbeddingFor(String str) {
        Objects.requireNonNull(str, "must provide an Faq instance");
        if (str.isBlank()) throw new IllegalArgumentException("cannot process an empty input");
        ModelApiSettings.ModelSettings embeddingSettings = this.settings.getEmbeddings();

        EmbeddingRequest payload = new EmbeddingRequest(
                embeddingSettings.getModel(),
                str);
        LOGGER.debug("reqeust: {}", payload);

        ResponseEntity<EmbeddingResponse> response = null;
        try {
            response = this.restTemplate.exchange(
                    this.settings.getEmbeddingUri(),
                    HttpMethod.POST,
                    buildRequest(payload),
                    EmbeddingResponse.class);
        } catch (RestClientException e) {
            LOGGER.error("failed to complete request", e);
            AIGateway.handleException(e);
        }

        LOGGER.debug( "response: {}", response.getBody());

        return !response.hasBody() ? null : response.getBody().data.get(0);
    }

    public Message submitChat(List<Message> messages) {
        Objects.requireNonNull(messages, "must provide messages");
        if (messages.isEmpty()) throw new IllegalArgumentException("must provide messages");
        ModelApiSettings.ModelSettings chatSettings = this.settings.getChat();

        ChatRequest request = new ChatRequest(
                chatSettings.getModel(),
                messages);
        LOGGER.debug("reqeust: {}", request);

        ResponseEntity<ChatResponse> response = null;
        try {
            response = this.restTemplate.exchange(
                    this.settings.getChatUri(),
                    HttpMethod.POST,
                    buildRequest(request),
                    ChatResponse.class);
        } catch (RestClientException e) {
            LOGGER.error("failed to complete request", e);
            AIGateway.handleException(e);
        }
        LOGGER.debug("response: {}", response.getBody());

        return !response.hasBody() ? null : response.getBody().choices[0].message;
    }

    private <T> HttpEntity<T> buildRequest(T payload) {
        Objects.requireNonNull(payload, "must provide a payload");
        return new HttpEntity<>(payload, this.buildHeaders());
    }

    private MultiValueMap<String, String> buildHeaders() {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        for (String key : this.settings.getHeaders().keySet()) {
            headers.add(key, this.settings.getHeaders().get(key));
        }
        return headers;
    }

}
