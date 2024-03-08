package net.malevy;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class RagApplication implements CommandLineRunner {

    private final Generator generator;
    private final ChatClient client;

    public static void main(String[] args) {
        SpringApplication.run(RagApplication.class, args);
    }

    public RagApplication(Generator generator, ChatClient client) {
        this.generator = generator;
        this.client = client;
    }

    @Override
    public void run(String... args) throws Exception {
//        this.generator.run();
        this.client.run();


    }
}
