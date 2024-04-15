package net.malevy;

import net.malevy.ai.AIGateway;
import net.malevy.ai.Embedding;
import net.malevy.ai.StopWordLoader;
import net.malevy.ai.openai.OpenAIApiGateway;
import net.malevy.faqs.Faq;
import net.malevy.faqs.FaqRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class Generator {

    final static Logger LOGGER = LoggerFactory.getLogger(Generator.class.getName());
    private final FaqRepository faqRepository;
    private final AIGateway modelGateway;
    private final RagSettings settings;

    public Generator(FaqRepository faqRepository, AIGateway modelGateway, RagSettings settings) {
        this.faqRepository = faqRepository;
        this.modelGateway = modelGateway;
        this.settings = settings;
    }

    public void run() {

        final int pageSize = this.settings.getGenerator().getBatchSize();
        int page = 0;
        while (true) {
            List<Faq> faqs = this.faqRepository.getFaqs(page, pageSize);
            if (faqs.isEmpty()) break;

            System.out.printf("retrieved %d FAQs\n", faqs.size());
            LOGGER.info(String.format("retrieved %d FAQs", faqs.size()));

            for (Faq faq : faqs) {
                String content = this.buildContent(faq);
                final Embedding embedding = this.modelGateway.getEmbeddingFor(content);
                this.faqRepository.writeEmbedding(faq, content, embedding);
            }

            System.out.printf("wrote %d FAQs\n", faqs.size());
            LOGGER.info(String.format("wrote %d embeddings", faqs.size()));
            page++;
        }
    }

    private String buildContent(Faq faq) {
        return "category: " + prepContent(faq.getCategory()) +
                "\nquestion: " + prepContent(faq.getQuestion()) +
                "\nanswer: " + prepContent(faq.getAnswer());
    }

    private String prepContent(String content) {
        final Set<String> stopWords = StopWordLoader.stopWords();
        List<String> words = List.of(content.split("\\s+"));
        return words.stream()
                .filter(w -> !stopWords.contains(w))
                .map(String::toLowerCase)
                .collect(Collectors.joining(" "));
    }

}
