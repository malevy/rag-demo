package net.malevy;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
public class ChatClient {

    private static String SYSTEM_MESSAGE_PROMPT_TEMPLATE = "You are an assistant for question-answering tasks.\n" +
            "Given the following CONTEXT and a question, create a final answer.\n" +
            "If you don't know the answer, just say that you don't know.\n" +
            "Don't try to make up an answer.\n" +
            "<CONTEXT>\n%s\n</CONTEXT>";

    private static String STOP_COMMAND = "/bye";
    private final FaqRepository faqRepository;
    private final OllamaGateway ollamaGateway;

    private final Scanner userInput = new Scanner(System.in);


    public ChatClient(FaqRepository faqRepository, OllamaGateway ollamaGateway) {
        this.faqRepository = faqRepository;
        this.ollamaGateway = ollamaGateway;
    }

    public void run() {

        displayIntro();
        while (true) {
            System.out.print("##> ");
            String input = userInput.nextLine();
            if (!couldBeValidInput(input)) continue;
            if (isExitCommand(input)) break;
            input = scrub(input);

            final Embedding inputEmbedding = ollamaGateway.getEmbeddingFor(input);
            List<Faq> faqs = faqRepository.findSimilar(inputEmbedding, 7);
            final String context = buildPromptContext(faqs);
            final Message system = Message.asSystem(String.format(SYSTEM_MESSAGE_PROMPT_TEMPLATE, context));
            final Message user = Message.asUser(input);
            final Message response = ollamaGateway.submitChat(List.of(system, user));
            System.out.println(response.getContent());

        }

    }

    private String buildPromptContext(List<Faq> faqs) {
        final StringBuilder sb = new StringBuilder();
        for(Faq faq : faqs) {
            sb.append(faq.toModelFriendlyString());
            sb.append("\n\n");
        }

        return sb.toString();
    }

    private void displayIntro() {
        System.out.println("Ask a question and I will try to answer it using the information " +
                "from the Tech Elevator FAQ library.");
        System.out.printf("type %s to stop.\n", STOP_COMMAND);
    }

    private String scrub(String input) {
        return input.trim();
    }

    private boolean couldBeValidInput(String input) {
        return !input.isBlank();
    }

    private boolean isExitCommand(String input) {
        return input.equalsIgnoreCase(STOP_COMMAND);
    }

}
