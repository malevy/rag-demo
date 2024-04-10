package net.malevy.ai.openai;

import com.fasterxml.jackson.annotation.JsonProperty;

class Usage {
    @JsonProperty("prompt_tokens")
    public int promptTokens;
    @JsonProperty("total_tokens")
    public int totalTokens;
}
