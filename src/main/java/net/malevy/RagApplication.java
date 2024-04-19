package net.malevy;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class RagApplication implements CommandLineRunner {

    private final ChatClient client;

    public static void main(String[] args) {
        SpringApplication.run(RagApplication.class, args);
    }

    public RagApplication(ChatClient client) {
        this.client = client;
    }

    @Override
    public void run(String... args) throws Exception {
        // disable headless mode so that we can chart the embeddings
        System.setProperty("java.awt.headless", "false");
        this.client.run();

    }
}
