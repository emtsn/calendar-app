package ui.exceptions;

public class InvalidCommandException extends InvalidInputException {
    public InvalidCommandException(String command) {
        super("'" + command + "' is not a recognized command.");
    }
}
