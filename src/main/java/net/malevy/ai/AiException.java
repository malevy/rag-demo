package net.malevy.ai;

/**
 * Thrown when an exception is returned from the AI
 */
public class AiException extends RuntimeException{

    public AiException(String message) {
        super(message);
    }

    public AiException(String message, Throwable cause) {
        super(message, cause);
    }
}
