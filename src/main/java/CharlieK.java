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

    /** Tracks whether each stored task has been completed. */
    private static final boolean[] completedTasks = new boolean[MAX_TASKS];

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
            } else if (command.startsWith("mark ")) {
                markTask(command.substring("mark ".length()));
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

    /**
     * Marks a task as done using its one-based position in the task list.
     *
     * @param taskNumberText the task number supplied after the {@code mark} command
     */
    private static void markTask(String taskNumberText) {
        try {
            int taskNumber = Integer.parseInt(taskNumberText);
            if (taskNumber < 1 || taskNumber > taskCount) {
                System.out.println("     That task does not exist.");
                return;
            }

            int taskIndex = taskNumber - 1;
            completedTasks[taskIndex] = true;
            System.out.println("     Nice! I've marked this task as done:");
            System.out.println("       [X] " + tasks[taskIndex]);
        } catch (NumberFormatException exception) {
            System.out.println("     Please provide a valid task number.");
        }
    }

    /** Displays all stored tasks and their completion status. */
    private static void printTasks() {
        System.out.println("     Here are the tasks in your list:");
        for (int i = 0; i < taskCount; i++) {
            String status = completedTasks[i] ? "X" : " ";
            System.out.println("     " + (i + 1) + ".[" + status + "] " + tasks[i]);
        }
    }
}
