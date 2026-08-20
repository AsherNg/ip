import java.util.Scanner;

/**
 * Runs the CharlieK command-line chatbot.
 */
public class CharlieK {
    private static final String LINE = "____________________________________________________________";
    private static final int MAX_TASKS = 100;

    /**
     * Stores the tasks entered during this run of the program.
     * The tasks are intentionally kept in memory only, as required.
     */
    private static final String[] tasks = new String[MAX_TASKS];

    /** Number of tasks currently stored. */
    private static int taskCount = 0;

    public static void main(String[] args) {
        String banner = "  ____ _                _ _      _  __\n"
                        + " / ___| |__   __ _ _ __| (_) ___| |/ /\n"
                        + "| |   | '_ \\ / _` | '__| | |/ _ \\ ' / \n"
                        + "| |___| | | | (_| | |  | | |  __/ . \\ \n"
                        + " \\____|_| |_|\\__,_|_|  |_|_|\\___|_|\\_\\\n";
                        
        System.out.println(LINE);
        System.out.print(banner);
        System.out.println("Hello! I'm CharlieK.");
        System.out.println("What can I do for you?");
        System.out.println(LINE);

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            System.out.println(LINE);

            if (command.equals("bye")) {
                System.out.println("     Bye. Hope to see you again soon!");
                System.out.println(LINE);
                break;
            }

            if (command.equals("list")) {
                printTasks();
            } else {
                addTask(command);
                System.out.println("     added: " + command);
            }
            System.out.println(LINE);
        }
    }

    /**
     * Adds a task to the in-memory task list.
     *
     * @param task the text entered by the user
     */
    private static void addTask(String task) {
        if (taskCount < MAX_TASKS) {
            tasks[taskCount] = task;
            taskCount++;
        }
    }

    /** Displays all stored tasks in the order they were entered. */
    private static void printTasks() {
        for (int i = 0; i < taskCount; i++) {
            System.out.println("     " + (i + 1) + ". " + tasks[i]);
        }
    }
}
