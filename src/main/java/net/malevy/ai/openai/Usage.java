package net.malevy.ai.openai;

import com.fasterxml.jackson.annotation.JsonProperty;

class Usage {
    @JsonProperty("prompt_tokens")
    public int promptTokens;
    @JsonProperty("total_tokens")
    public int totalTokens;
    @JsonProperty("completion_tokens")
    public int completionTokens;

    @Override
    public String toString() {
        return "Usage{" +
                "promptTokens=" + promptTokens +
                ", totalTokens=" + totalTokens +
                ", completionTokens=" + completionTokens +
                '}';
    }
}
