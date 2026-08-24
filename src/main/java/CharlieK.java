import java.nio.file.Path;
import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Runs the CharlieK command-line chatbot.
 */
public class CharlieK {
    private static final String LINE = "____________________________________________________________";

    /** Stores the tasks entered during this run of the program. */
    private static final ArrayList<Task> tasks = new ArrayList<>();

    /** The relative path where the current task list is saved. */
    private static final Path TASK_FILE = Path.of("data", "charliek.csv");

    /** Provides the task list's file-system persistence. */
    private static final Storage STORAGE = new Storage(TASK_FILE);

    public static void main(String[] args) {
        String loadingError = null;
        try {
            loadTasks();
        } catch (TaskStorageException exception) {
            loadingError = exception.getMessage();
        }

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
        if (loadingError != null) {
            System.out.println("     " + loadingError);
            System.out.println(LINE);
        }

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            System.out.println(LINE);

            try {
                Command commandType = Command.fromInput(command)
                        .orElseThrow(UnknownCommandException::new);

                if (commandType == Command.BYE) {
                    System.out.println("     Bye. Hope to see you again soon!");
                    System.out.println(LINE);
                    break;
                }

                switch (commandType) {
                case LIST:
                    printTasks();
                    break;
                case MARK:
                    markTask(commandType.argumentFrom(command));
                    break;
                case UNMARK:
                    unmarkTask(commandType.argumentFrom(command));
                    break;
                case DELETE:
                    deleteTask(commandType.argumentFrom(command));
                    break;
                case TODO:
                    addToDo(commandType.argumentFrom(command));
                    break;
                case DEADLINE:
                    addDeadline(commandType.argumentFrom(command));
                    break;
                case EVENT:
                    addEvent(commandType.argumentFrom(command));
                    break;
                default:
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
    private static void addTask(Task task) throws TaskStorageException {
        tasks.add(task);
        try {
            saveTasks();
        } catch (TaskStorageException exception) {
            tasks.remove(tasks.size() - 1);
            throw exception;
        }
    }

    /** Saves the current task list as one CSV row per task. */
    private static void saveTasks() throws TaskStorageException {
        STORAGE.save(tasks);
    }

    /**
     * Loads task lines saved by {@link #saveTasks()} when the application starts.
     * Missing files represent a new, empty task list.
     */
    private static void loadTasks() throws TaskStorageException {
        ArrayList<Task> loadedTasks = STORAGE.load();
        tasks.clear();
        tasks.addAll(loadedTasks);
    }

    /** Adds a typed task and prints the confirmation shown by the user interface. */
    private static void addTypedTask(Task task) throws TaskStorageException {
        addTask(task);
        System.out.println("     Got it. I've added this task:");
        System.out.println("       " + task);
        System.out.println("     Now you have " + tasks.size() + " tasks in the list.");
    }

    /** Parses and adds a to-do command. */
    private static void addToDo(String command)
            throws EmptyTaskDescriptionException, TaskStorageException {
        String description = command.trim();
        if (description.isEmpty()) {
            throw new EmptyTaskDescriptionException();
        }

        addTypedTask(new ToDo(description));
    }

    /** Parses and adds a deadline command. */
    private static void addDeadline(String command)
            throws EmptyTaskDescriptionException, EmptyParameterException,
            InvalidDateTimeException, TaskStorageException {
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

        try {
            addTypedTask(new Deadline(description, DateTimeParser.parseUserInput(deadline)));
        } catch (DateTimeException exception) {
            throw new InvalidDateTimeException();
        }
    }

    /** Parses and adds an event command. */
    private static void addEvent(String command)
            throws EmptyTaskDescriptionException, EmptyParameterException,
            InvalidDateTimeException, TaskStorageException {
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

        try {
            addTypedTask(new Event(description,
                    DateTimeParser.parseUserInput(from), DateTimeParser.parseUserInput(to)));
        } catch (DateTimeException exception) {
            throw new InvalidDateTimeException();
        }
    }

    /**
     * Marks a task as done using its one-based position in the task list.
     *
     * @param taskNumberText the task number supplied after the {@code mark} command
     */
    private static void markTask(String taskNumberText) throws TaskStorageException {
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
            try {
                saveTasks();
            } catch (TaskStorageException exception) {
                task.markAsNotDone();
                throw exception;
            }
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
    private static void unmarkTask(String taskNumberText) throws TaskStorageException {
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
            try {
                saveTasks();
            } catch (TaskStorageException exception) {
                task.markAsDone();
                throw exception;
            }
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
    private static void deleteTask(String taskNumberText) throws TaskStorageException {
        try {
            int taskNumber = Integer.parseInt(taskNumberText);
            if (taskNumber < 1 || taskNumber > tasks.size()) {
                System.out.println("     That task does not exist.");
                return;
            }

            int taskIndex = taskNumber - 1;
            Task deletedTask = tasks.remove(taskIndex);
            try {
                saveTasks();
            } catch (TaskStorageException exception) {
                tasks.add(taskIndex, deletedTask);
                throw exception;
            }

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
