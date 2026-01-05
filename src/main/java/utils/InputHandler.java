package utils;

import exceptions.InvalidInputException;

import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;

public class InputHandler {
    private final Scanner scanner;

    public InputHandler(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Prompts for input until not blank or user quits.
     */
    public String promptForNonBlankString(String prompt, String errorMessage) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("Q")) {
                return null;
            }

            try {
                ValidationUtils.validateNonEmpty(input, "Input");
                return input;
            } catch (InvalidInputException ex) {
                System.out.println(errorMessage);
            }

        }
    }

    /**
     * Prompts for choice selection until valid choice or user quits.
     */
    public String promptForChoice(String prompt, Map<String, String> options) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().toLowerCase();

            if (input.equalsIgnoreCase("Q")) {
                return null;
            }

            if (options.containsKey(input)) {
                return options.get(input);
            }

            System.out.println("Invalid option. Please try again.");
        }
    }

    /**
     * Prompts for input until valid or user quits.
     */
    public String promptUntilValid(String prompt,
                                   Function<String, Boolean> validator,
                                   String errorMessage) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("Q")) {
                return null;
            }

            if (validator.apply(input)) {
                return input;
            }

            System.out.println(errorMessage);
        }
    }


    public Boolean promptForConfirmation(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("Q")) {
                return null;
            }

            try {
                ValidationUtils.validateNonEmpty(input, "Option choice");
                switch (input.toUpperCase()) {
                    case "Y":
                    case "YES":
                        return true;
                    case "N":
                    case "NO":
                        return false;
                    default:
                        System.out.println("Invalid option. Try again");
                }
            } catch (InvalidInputException e) {
                System.out.println("Invalid option. Try again");
            }
        }
    }
}