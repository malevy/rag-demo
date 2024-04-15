package net.malevy;

import net.malevy.ai.AIGateway;
import net.malevy.ai.AiException;
import net.malevy.ai.Embedding;
import net.malevy.ai.Message;
import net.malevy.faqs.Chunk;
import net.malevy.faqs.FaqRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;


@Component
public class ChatClient {

    final static Logger LOGGER = LoggerFactory.getLogger(ChatClient.class);

    private static String SYSTEM_MESSAGE_PROMPT_TEMPLATE =
            "You are an assistant for question-answering tasks.\n" +
                    "Given the following CONTEXT and a question, create a final answer.\n" +
                    "if you don't understand the question, ask a clarifying question.\n" +
                    "If you don't know the answer, just say 'I do not know the answer to that question'. Don't try to make up an answer.\n" +
                    "Keep the style friendly but business appropriate.\n" +
                    "### CONTEXT ###\n%s";

    private final static String STOP_COMMAND = "/bye";

    private final FaqRepository faqRepository;
    private final AIGateway aiGateway;
    private final RagSettings settings;

    private final Scanner userInput = new Scanner(System.in);

    public ChatClient(FaqRepository faqRepository, AIGateway aiGateway, RagSettings settings) {
        this.faqRepository = faqRepository;
        this.aiGateway = aiGateway;
        this.settings = settings;
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

            final List<String> questions = expandQuestion(input);
            final Set<Chunk> chunks = getApplicableFaqs(questions);
            final String context = buildPromptContext(chunks);
            final Message system = Message.asSystem(String.format(SYSTEM_MESSAGE_PROMPT_TEMPLATE, context));
            final Message user = Message.asUser(input);
            String fromModel;
            try {
                final Message response = aiGateway.submitChat(List.of(system, user));
                fromModel = response == null || StringUtils.isEmpty(response.getContent()) ? "Sorry. I have no answer." : response.getContent();
            } catch (AiException e) {
                fromModel = String.format("{ answer unavailable: %s }", e.getMessage());
            }
            System.out.println(fromModel);
        }
        LOGGER.info("Stopping chat client");
    }

    private Set<Chunk> getApplicableFaqs(List<String> questions) {
        final Set<Chunk> faqs = new HashSet<>();
        RagSettings.ChatSettings chatSettings = settings.getChat();
        for (String question : questions) {
            final Embedding inputEmbedding = aiGateway.getEmbeddingFor(question);
            final List<Chunk> similarFaqs = faqRepository.findSimilar(inputEmbedding,
                    chatSettings.getTopk(),
                    chatSettings.getSimilarityThreshold());
            faqs.addAll(similarFaqs);
        }
        return faqs;
    }

    private String buildPromptContext(Collection<Chunk> chunks) {
        final StringBuilder sb = new StringBuilder();
        for (Chunk chunk : chunks) {
            sb.append(chunk.content);
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

    private List<String> expandQuestion(String question) {
        final String prompt = "Generate %d different versions or rephrasings of the following question." +
                "Put each question on a new line without any leading characters like hyphens or numbers.";
        final Message system = Message.asSystem(String.format(prompt, settings.getChat().getExpansionCount()));
        final Message user = Message.asUser("question:" + question);
        List<String> questions = new ArrayList<>();

        try {
            final Message response = aiGateway.submitChat(List.of(system, user));
            if (response != null && !StringUtils.isEmpty(response.getContent())) {
                final String fromModel = response.getContent();
                for(String newQ : fromModel.split("\n")) {
                    if (!StringUtils.isEmpty(newQ)) {
                        questions.add(newQ);
                    }
                }
            }
        } catch (AiException e) {
            LOGGER.error("could not expand question", e);
        }

        if (questions.isEmpty()) {
            questions.add(question);
        }

        LOGGER.debug("expanded questions: {}", questions);
        return questions;
    }

}
