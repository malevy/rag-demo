package net.malevy.ai;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;

public interface AIGateway {
    Embedding getEmbeddingFor(String text);
    Message submitChat(List<Message> messages);

    public static void handleException(RestClientException rce) {
        if (rce instanceof RestClientResponseException) {
            RestClientResponseException rcre = (RestClientResponseException) rce;
            HttpStatus status = HttpStatus.resolve(rcre.getRawStatusCode());
            switch (status) {
                case BAD_REQUEST:
                    throw new AiException("invalid request", rcre);
                case INTERNAL_SERVER_ERROR:
                    throw new AiException("could not process the request", rcre);
                default:
                    throw new AiException("unexpected problem", rcre);
            }
        } else if (rce instanceof ResourceAccessException) {
            throw new AiException("could not process the request", rce);
        } else {
            throw new AiException("unexpected problem", rce);
        }
    }
}
