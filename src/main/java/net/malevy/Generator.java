package net.malevy;

import net.malevy.ai.Embedding;
import net.malevy.ai.ModelApiGateway;
import net.malevy.faqs.Faq;
import net.malevy.faqs.FaqRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Generator {

    final static Logger LOGGER = LoggerFactory.getLogger(Generator.class.getName());
    private final FaqRepository faqRepository;
    private final ModelApiGateway modelGateway;

    public Generator(FaqRepository faqRepository, ModelApiGateway modelGateway) {
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
