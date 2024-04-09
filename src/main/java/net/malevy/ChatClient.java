package net.malevy;

import net.malevy.ai.Embedding;
import net.malevy.ai.Message;
import net.malevy.ai.ModelApiGateway;
import net.malevy.faqs.Faq;
import net.malevy.faqs.FaqRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Scanner;


@Component
public class ChatClient {

    final static Logger LOGGER = LoggerFactory.getLogger(ChatClient.class);

    private static String SYSTEM_MESSAGE_PROMPT_TEMPLATE = "You are an assistant for question-answering tasks.\n" +
            "Given the following CONTEXT and a question, create a final answer.\n" +
            "If you don't know the answer, just say that you don't know.\n" +
            "Don't try to make up an answer.\n" +
            "<CONTEXT>\n%s\n</CONTEXT>";

    private static String STOP_COMMAND = "/bye";
    private final FaqRepository faqRepository;
    private final ModelApiGateway ollamaGateway;

    private final Scanner userInput = new Scanner(System.in);


    public ChatClient(FaqRepository faqRepository, ModelApiGateway modelApiGateway) {
        this.faqRepository = faqRepository;
        this.ollamaGateway = modelApiGateway;
    }

    public void run() {

        LOGGER.info("Starting chat client");
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
            String fromModel = StringUtils.isEmpty(response.getContent()) ? "{ no response from the model }" : response.getContent();
            System.out.println(fromModel);

        }
        LOGGER.info("Stopping chat client");

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
