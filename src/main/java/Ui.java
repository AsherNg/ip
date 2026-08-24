import java.util.Scanner;

/**
 * Handles CharlieK's basic interaction with the user.
 *
 * <p>This class owns console input and the common session messages. More
 * specific task messages can be moved here in a later extraction.</p>
 */
public class Ui {
    /** The separator printed between console messages. */
    private static final String LINE = "____________________________________________________________";

    /** The banner printed when CharlieK starts. */
    private static final String BANNER = "  ____ _                _ _      _  __\n"
            + " / ___| |__   __ _ _ __| (_) ___| |/ /\n"
            + "| |   | '_ \\ / _` | '__| | |/ _ \\ ' / \n"
            + "| |___| | | | (_| | |  | | |  __/ . \\ \n"
            + " \\____|_| |_|\\__,_|_|  |_|_|\\___|_|\\_\\\n";

    /** Reads commands from the user's standard input. */
    private final Scanner scanner;

    /** Creates a UI connected to the standard console. */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Shows the startup banner and any saved-task loading error.
     *
     * @param loadingError the loading error to show, or {@code null} when loading succeeded
     */
    public void showWelcome(String loadingError) {
        showLine();
        System.out.print(BANNER);
        System.out.println("Hello! I'm CharlieK.");
        System.out.println("What can I do for you?");
        showLine();
        if (loadingError != null) {
            showError(loadingError);
            showLine();
        }
    }

    /**
     * Returns whether another command is available from standard input.
     *
     * @return {@code true} when another input line is available
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command from standard input.
     *
     * @return the next complete input line
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Prints the standard message separator. */
    public void showLine() {
        System.out.println(LINE);
    }

    /** Shows the normal goodbye message and its trailing separator. */
    public void showGoodbye() {
        System.out.println("     Bye. Hope to see you again soon!");
        showLine();
    }

    /**
     * Shows a user-facing error message.
     *
     * @param message the message to display
     */
    public void showError(String message) {
        System.out.println("     " + message);
    }

    /** Shows the fallback message used for unexpected command-processing failures. */
    public void showProcessingError() {
        showError("I couldn't process that command. Please check the input and try again.");
    }
}
