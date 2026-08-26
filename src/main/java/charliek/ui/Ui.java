package charliek.ui;

import charliek.model.Task;
import java.util.Scanner;
import java.util.List;

/**
 * Handles CharlieK's basic interaction with the user.
 *
 * <p>This class owns console input and all user-facing messages. It does not
 * decide how tasks are mutated or sorted.</p>
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

    /**
     * Shows confirmation that a task was added.
     *
     * @param task the added task
     * @param taskCount the number of tasks after adding it
     */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println("     Got it. I've added this task:");
        System.out.println("       " + task);
        System.out.println("     Now you have " + taskCount + " tasks in the list.");
    }

    /** Shows that the requested task number does not exist. */
    public void showTaskDoesNotExist() {
        showError("That task does not exist.");
    }

    /** Shows that the supplied task number is not a valid number. */
    public void showInvalidTaskNumber() {
        showError("Please provide a valid task number.");
    }

    /**
     * Shows confirmation that a task was marked as done.
     *
     * @param task the marked task
     */
    public void showTaskMarked(Task task) {
        System.out.println("     Nice! I've marked this task as done:");
        System.out.println("       " + task);
    }

    /**
     * Shows that a task was already marked as done.
     *
     * @param task the already marked task
     */
    public void showTaskAlreadyMarked(Task task) {
        System.out.println("     This task is already marked:");
        System.out.println("       " + task);
    }

    /**
     * Shows confirmation that a task was marked as not done.
     *
     * @param task the unmarked task
     */
    public void showTaskUnmarked(Task task) {
        System.out.println("     OK, I've marked this task as not done yet:");
        System.out.println("       " + task);
    }

    /**
     * Shows that a task was already marked as not done.
     *
     * @param task the already unmarked task
     */
    public void showTaskAlreadyUnmarked(Task task) {
        System.out.println("     This task is already unmarked:");
        System.out.println("       " + task);
    }

    /**
     * Shows confirmation that a task was deleted.
     *
     * @param task the deleted task
     * @param taskCount the number of tasks after deleting it
     */
    public void showTaskDeleted(Task task, int taskCount) {
        System.out.println("     Noted. I've removed this task:");
        System.out.println("       " + task);
        System.out.println("     Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Shows the supplied tasks in their display order.
     *
     * @param tasks the tasks to display
     */
    public void showTasks(List<Task> tasks) {
        System.out.println("     Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println("     " + (i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Shows tasks that match a keyword search.
     *
     * @param tasks the matching tasks to display
     */
    public void showMatchingTasks(List<Task> tasks) {
        System.out.println("     Here are the matching tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println("     " + (i + 1) + "." + tasks.get(i));
        }
    }
}
