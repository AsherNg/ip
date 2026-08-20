import java.util.ArrayList;
import java.util.Scanner;

/**
 * Runs the CharlieK command-line chatbot.
 */
public class CharlieK {
    private static final String LINE = "____________________________________________________________";

    /**
     * Stores the tasks entered during this run of the program.
     * The tasks are intentionally kept in memory only, as required.
     */
    private static final ArrayList<Task> tasks = new ArrayList<>();

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

            try {
                if (command.equals("bye")) {
                    System.out.println("     Bye. Hope to see you again soon!");
                    System.out.println(LINE);
                    break;
                }

                if (command.equals("list")) {
                    printTasks();
                } else if (command.startsWith("mark ")) {
                    markTask(command.substring("mark ".length()));
                } else if (command.startsWith("unmark ")) {
                    unmarkTask(command.substring("unmark ".length()));
                } else if (command.startsWith("delete ")) {
                    deleteTask(command.substring("delete ".length()));
                } else if (command.equals("todo") || command.startsWith("todo ")) {
                    addToDo(command.substring("todo".length()));
                } else if (command.equals("deadline") || command.startsWith("deadline ")) {
                    addDeadline(command.substring("deadline".length()));
                } else if (command.equals("event") || command.startsWith("event ")) {
                    addEvent(command.substring("event".length()));
                } else {
                    throw new UnknownCommandException();
                }
            } catch (CharlieKException exception) {
                System.out.println("     " + exception.getMessage());
            }
            System.out.println(LINE);
        }
    }

    /**
     * Adds a task to the in-memory task list.
     *
     * @param task the task object to store
     */
    private static void addTask(Task task) {
        tasks.add(task);
    }

    /** Adds a typed task and prints the confirmation shown by the user interface. */
    private static void addTypedTask(Task task) {
        addTask(task);
        System.out.println("     Got it. I've added this task:");
        System.out.println("       " + task);
        System.out.println("     Now you have " + tasks.size() + " tasks in the list.");
    }

    /** Parses and adds a to-do command. */
    private static void addToDo(String command) throws EmptyTaskDescriptionException {
        String description = command.trim();
        if (description.isEmpty()) {
            throw new EmptyTaskDescriptionException();
        }

        addTypedTask(new ToDo(description));
    }

    /** Parses and adds a deadline command. */
    private static void addDeadline(String command)
            throws EmptyTaskDescriptionException, EmptyParameterException {
        String commandText = command.trim();
        if (commandText.isEmpty()) {
            throw new EmptyTaskDescriptionException();
        }

        int markerIndex = commandText.indexOf(" /by ");
        String description = markerIndex < 0
                ? commandText
                : commandText.substring(0, markerIndex).trim();
        if (description.isEmpty()) {
            throw new EmptyTaskDescriptionException();
        }
        if (markerIndex < 0) {
            throw new EmptyParameterException();
        }

        String deadline = commandText.substring(markerIndex + " /by ".length()).trim();
        if (deadline.isEmpty()) {
            throw new EmptyParameterException();
        }

        addTypedTask(new Deadline(description, deadline));
    }

    /** Parses and adds an event command. */
    private static void addEvent(String command)
            throws EmptyTaskDescriptionException, EmptyParameterException {
        String commandText = command.trim();
        if (commandText.isEmpty()) {
            throw new EmptyTaskDescriptionException();
        }

        int fromMarkerIndex = commandText.indexOf(" /from ");
        String description = fromMarkerIndex < 0
                ? commandText
                : commandText.substring(0, fromMarkerIndex).trim();
        if (description.isEmpty()) {
            throw new EmptyTaskDescriptionException();
        }

        int toMarkerIndex = commandText.indexOf(" /to ", fromMarkerIndex + 1);
        if (fromMarkerIndex < 0 || toMarkerIndex < 0) {
            throw new EmptyParameterException();
        }

        String from = commandText.substring(fromMarkerIndex + " /from ".length(), toMarkerIndex).trim();
        String to = commandText.substring(toMarkerIndex + " /to ".length()).trim();
        if (from.isEmpty() || to.isEmpty()) {
            throw new EmptyParameterException();
        }

        addTypedTask(new Event(description, from, to));
    }

    /**
     * Marks a task as done using its one-based position in the task list.
     *
     * @param taskNumberText the task number supplied after the {@code mark} command
     */
    private static void markTask(String taskNumberText) {
        try {
            int taskNumber = Integer.parseInt(taskNumberText);
            if (taskNumber < 1 || taskNumber > tasks.size()) {
                System.out.println("     That task does not exist.");
                return;
            }

            int taskIndex = taskNumber - 1;
            Task task = tasks.get(taskIndex);
            if (task.isDone()) {
                System.out.println("     This task is already marked:");
                System.out.println("       " + task);
                return;
            }

            task.markAsDone();
            System.out.println("     Nice! I've marked this task as done:");
            System.out.println("       " + task);
        } catch (NumberFormatException exception) {
            System.out.println("     Please provide a valid task number.");
        }
    }

    /**
     * Marks a task as not done using its one-based position in the task list.
     *
     * @param taskNumberText the task number supplied after the {@code unmark} command
     */
    private static void unmarkTask(String taskNumberText) {
        try {
            int taskNumber = Integer.parseInt(taskNumberText);
            if (taskNumber < 1 || taskNumber > tasks.size()) {
                System.out.println("     That task does not exist.");
                return;
            }

            int taskIndex = taskNumber - 1;
            Task task = tasks.get(taskIndex);
            if (!task.isDone()) {
                System.out.println("     This task is already unmarked:");
                System.out.println("       " + task);
                return;
            }

            task.markAsNotDone();
            System.out.println("     OK, I've marked this task as not done yet:");
            System.out.println("       " + task);
        } catch (NumberFormatException exception) {
            System.out.println("     Please provide a valid task number.");
        }
    }

    /**
     * Deletes a task using its one-based position in the task list.
     *
     * @param taskNumberText the task number supplied after the {@code delete} command
     */
    private static void deleteTask(String taskNumberText) {
        try {
            int taskNumber = Integer.parseInt(taskNumberText);
            if (taskNumber < 1 || taskNumber > tasks.size()) {
                System.out.println("     That task does not exist.");
                return;
            }

            int taskIndex = taskNumber - 1;
            Task deletedTask = tasks.remove(taskIndex);

            System.out.println("     Noted. I've removed this task:");
            System.out.println("       " + deletedTask);
            System.out.println("     Now you have " + tasks.size() + " tasks in the list.");
        } catch (NumberFormatException exception) {
            System.out.println("     Please provide a valid task number.");
        }
    }

    /** Displays all stored tasks and their completion status. */
    private static void printTasks() {
        System.out.println("     Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println("     " + (i + 1) + "." + tasks.get(i));
        }
    }
}
