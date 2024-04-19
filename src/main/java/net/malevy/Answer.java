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
public class Answer {

    private static Logger LOGGER = LoggerFactory.getLogger(Answer.class);
    private static String SYSTEM_MESSAGE_PROMPT_TEMPLATE =
            "You are an assistant for question-answering tasks.\n" +
                    "Given the following CONTEXT and a question, create a final answer.\n" +
                    "if you don't understand the question, ask a clarifying question.\n" +
                    "If you don't know the answer, just say 'I do not know the answer to that question'. Don't try to make up an answer.\n" +
                    "Keep the style friendly but business appropriate.\n" +
                    "### CONTEXT ###\n%s";
    final static String EXPAND_QUESTION_PROMPT = "Generate %d different versions or rephrasings of the following question." +
            "Put each question on a new line without any leading characters like hyphens or numbers.";

    private final FaqRepository faqRepository;
    private final AIGateway aiGateway;
    private final RagSettings settings;

    public Answer(FaqRepository faqRepository, AIGateway aiGateway, RagSettings settings) {
        this.faqRepository = faqRepository;
        this.aiGateway = aiGateway;
        this.settings = settings;
    }

    public String process(String input) {
        final String cleansedInput = input.trim().replaceAll("[^a-zA-Z0-9\\s]", "");
        final List<String> questions = expandQuestion(cleansedInput);
        final Set<Chunk> chunks = getApplicableFaqs(questions);
        final String context = buildPromptContext(chunks);
        final Message system = Message.asSystem(String.format(SYSTEM_MESSAGE_PROMPT_TEMPLATE, context));
        final Message user = Message.asUser(cleansedInput);
        String fromModel;
        try {
            final Message response = aiGateway.submitChat(List.of(system, user));
            fromModel = response == null || StringUtils.isEmpty(response.getContent()) ? "Sorry. I have no answer." : response.getContent();
        } catch (AiException e) {
            fromModel = String.format("{ answer unavailable: %s }", e.getMessage());
        }
        return fromModel;
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

    private List<String> expandQuestion(String question) {
        final Message system = Message.asSystem(String.format(EXPAND_QUESTION_PROMPT, settings.getChat().getExpansionCount()));
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
