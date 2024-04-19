package net.malevy;

import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class UserInterface {

    private final Scanner userInput = new Scanner(System.in);

    public void write(String str) {
        System.out.println(str);
    }

    public void write(String format, Object... args) {
        write(String.format(format, args));
    }

    public String ask(String prompt) {
        System.out.print(prompt);
        return userInput.nextLine();
    }

}
