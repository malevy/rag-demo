package net.malevy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ChatClient {

    final static Logger LOGGER = LoggerFactory.getLogger(ChatClient.class);

    private final static String STOP_COMMAND = "/bye";
    private final static String GENERATE_COMMAND = "/generate";
    private final static String GRAPH_COMMAND = "/graph";

    private final UserInterface ui;
    private final Generator generator;
    private final Grapher grapher;
    private final Answer answer;

    public ChatClient(UserInterface ui, Generator generator, Grapher grapher, Answer answer) {
        this.ui = ui;
        this.generator = generator;
        this.grapher = grapher;
        this.answer = answer;
    }

    public void run() {
        LOGGER.info("Starting client");

        displayIntro();
        while (true) {
            String input = ui.ask("##>");
            if (!couldBeValidInput(input)) continue;
            input = scrub(input);
            if (hasCommand(input, STOP_COMMAND)) break;
            if (hasCommand(input, GENERATE_COMMAND)) {
                generator.run();
                continue;
            }
            if(hasCommand(input, GRAPH_COMMAND)) {
                grapher.generate(input.replace(GRAPH_COMMAND, ""));
                continue;
            }
            ui.write(answer.process(input));
        }
        LOGGER.info("Stopping client");
    }

    private void displayIntro() {
        ui.write("Ask a question and I will try to answer it using the information " +
                "from the Tech Elevator FAQ library.");
        ui.write("type %s to stop.\n", STOP_COMMAND);
        ui.write("type %s to regenerate the embeddings.\n", GENERATE_COMMAND);
    }

    private String scrub(String input) {
        return input.trim();
    }

    private boolean couldBeValidInput(String input) {
        return !input.isBlank();
    }

    private boolean hasCommand(String input, String command) {
        return input.toLowerCase().startsWith(command);
    }


}
