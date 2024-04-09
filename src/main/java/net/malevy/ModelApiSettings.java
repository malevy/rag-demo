package net.malevy;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.util.Lazy;
import org.springframework.web.util.UriComponentsBuilder;

@ConfigurationProperties(prefix = "api")
public class ModelApiSettings {

    private String host;
    private String embeddingPath;
    private String embeddingModel;
    private String chatPath;
    private String chatModel;

    private final Lazy<String> chatUri = new Lazy<>(() -> makeUri(host, chatPath));
    private final Lazy<String> embeddingUri = new Lazy<>(() -> makeUri(host, embeddingPath));

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getEmbeddingPath() {
        return embeddingPath;
    }

    public void setEmbeddingPath(String embeddingPath) {
        this.embeddingPath = embeddingPath;
    }

    public String getEmbeddingModel() {
        return embeddingModel;
    }

    public void setEmbeddingModel(String embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    public String getChatPath() {
        return chatPath;
    }

    public void setChatPath(String chatPath) {
        this.chatPath = chatPath;
    }

    public String getChatModel() {
        return chatModel;
    }

    public void setChatModel(String chatModel) {
        this.chatModel = chatModel;
    }

    public String getChatUri() {
        return chatUri.get();
    }

    public String getEmbeddingUri() {
        return embeddingUri.get();
    }

    private static String makeUri(String host, String path) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(host);
        builder.path(path);
        return builder.toUriString();

    }
}
