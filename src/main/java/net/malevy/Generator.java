package net.malevy;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.logging.Logger;

@Component
public class Generator {

    final static Logger LOGGER = Logger.getLogger(Generator.class.getName());
    private final FaqRepository faqRepository;
    private final OllamaGateway modelGateway;

    public Generator(FaqRepository faqRepository, OllamaGateway modelGateway) {
        this.faqRepository = faqRepository;
        this.modelGateway = modelGateway;
    }

    public void run() {

        final int pageSize = 20;
        boolean processing = true;
        int page = 0;
        while (true) {
            List<Faq> faqs = this.faqRepository.getFaqs(page, pageSize);
            if (faqs.isEmpty()) break;

            LOGGER.info(String.format("retrieved %d FAQs", faqs.size()));

            for (Faq faq : faqs) {
                final Embedding embedding = this.modelGateway.getEmbeddingFor(faq);
                this.faqRepository.writeEmbedding(faq, embedding);
            }

            LOGGER.info(String.format("wrote %d embeddings", faqs.size()));
            page++;
        }


    }

}
