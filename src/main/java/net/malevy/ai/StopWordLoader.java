package net.malevy.ai;

import java.util.Set;

public class StopWordLoader {
    public static Set<String> stopWords() {
        return Set.of("the",
                "and",
                "a",
                "an",
                "in",
                "on",
                "at",
                "to",
                "for"
        );
    }
}
