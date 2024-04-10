package net.malevy.ai;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.util.Lazy;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@ConfigurationProperties(prefix = "api")
public class ModelApiSettings {

    private String host;
    private ModelSettings embeddings;
    private ModelSettings chat;

    private Map<String, String> headers;

    private final Lazy<String> chatUri = new Lazy<>(() -> makeUri(host, this.chat.getPath()));
    private final Lazy<String> embeddingUri = new Lazy<>(() -> makeUri(host, this.embeddings.getPath()));

    public ModelSettings getEmbeddings() {
        return embeddings;
    }

    public void setEmbeddings(ModelSettings embeddings) {
        this.embeddings = embeddings;
    }

    public ModelSettings getChat() {
        return chat;
    }

    public void setChat(ModelSettings chat) {
        this.chat = chat;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getChatUri() {
        return chatUri.get();
    }

    public String getEmbeddingUri() {
        return embeddingUri.get();
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    private static String makeUri(String host, String path) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(host);
        builder.path(path);
        return builder.toUriString();

    }

    public static class ModelSettings {
        private String path;
        private String model;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

    }


}