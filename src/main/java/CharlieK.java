import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

/**
 * Runs the CharlieK command-line chatbot.
 */
public class CharlieK {
    /** Stores the tasks entered during this run of the program. */
    private static final TaskList tasks = new TaskList();

    /** The relative path where the current task list is saved. */
    private static final Path TASK_FILE = Path.of("data", "charliek.csv");

    /** Provides the task list's file-system persistence. */
    private static final Storage STORAGE = new Storage(TASK_FILE);

    /** Interprets command lines entered by the user. */
    private static final Parser PARSER = new Parser();

    /** Handles console input and common session messages. */
    private static final Ui UI = new Ui();

    public static void main(String[] args) {
        String loadingError = null;
        try {
            loadTasks();
        } catch (TaskStorageException exception) {
            loadingError = exception.getMessage();
        } catch (RuntimeException exception) {
            loadingError = "I couldn't load saved tasks because the saved data is invalid.";
        }

        UI.showWelcome(loadingError);

        while (UI.hasNextCommand()) {
            String command = UI.readCommand();
            UI.showLine();

            try {
                Parser.ParsedCommand parsedCommand = PARSER.parse(command);
                Command commandType = parsedCommand.command();
                String argument = parsedCommand.argument();

                if (commandType == Command.BYE) {
                    UI.showGoodbye();
                    break;
                }

                switch (commandType) {
                case LIST:
                    printTasks(argument.trim());
                    break;
                case MARK:
                    markTask(argument);
                    break;
                case UNMARK:
                    unmarkTask(argument);
                    break;
                case DELETE:
                    deleteTask(argument);
                    break;
                case TODO:
                    addTypedTask(PARSER.parseToDo(argument));
                    break;
                case DEADLINE:
                    addTypedTask(PARSER.parseDeadline(argument));
                    break;
                case EVENT:
                    addTypedTask(PARSER.parseEvent(argument));
                    break;
                default:
                    throw new UnknownCommandException();
                }
            } catch (CharlieKException exception) {
                UI.showError(exception.getMessage());
            } catch (RuntimeException exception) {
                UI.showProcessingError();
            }
            UI.showLine();
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
        } catch (RuntimeException exception) {
            tasks.remove(tasks.size() - 1);
            throw exception;
        }
    }

    /** Saves the current task list as one CSV row per task. */
    private static void saveTasks() throws TaskStorageException {
        STORAGE.save(tasks.toList());
    }

    /**
     * Loads task lines saved by {@link #saveTasks()} when the application starts.
     * Missing files represent a new, empty task list.
     */
    private static void loadTasks() throws TaskStorageException {
        tasks.replaceWith(STORAGE.load());
    }

    /** Adds a typed task and prints the confirmation shown by the user interface. */
    private static void addTypedTask(Task task) throws TaskStorageException {
        addTask(task);
        UI.showTaskAdded(task, tasks.size());
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
                UI.showTaskDoesNotExist();
                return;
            }

            int taskIndex = taskNumber - 1;
            Task task = tasks.get(taskIndex);
            if (task.isDone()) {
                UI.showTaskAlreadyMarked(task);
                return;
            }

            task.markAsDone();
            try {
                saveTasks();
            } catch (TaskStorageException exception) {
                task.markAsNotDone();
                throw exception;
            } catch (RuntimeException exception) {
                task.markAsNotDone();
                throw exception;
            }
            UI.showTaskMarked(task);
        } catch (NumberFormatException exception) {
            UI.showInvalidTaskNumber();
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
                UI.showTaskDoesNotExist();
                return;
            }

            int taskIndex = taskNumber - 1;
            Task task = tasks.get(taskIndex);
            if (!task.isDone()) {
                UI.showTaskAlreadyUnmarked(task);
                return;
            }

            task.markAsNotDone();
            try {
                saveTasks();
            } catch (TaskStorageException exception) {
                task.markAsDone();
                throw exception;
            } catch (RuntimeException exception) {
                task.markAsDone();
                throw exception;
            }
            UI.showTaskUnmarked(task);
        } catch (NumberFormatException exception) {
            UI.showInvalidTaskNumber();
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
                UI.showTaskDoesNotExist();
                return;
            }

            int taskIndex = taskNumber - 1;
            Task deletedTask = tasks.remove(taskIndex);
            try {
                saveTasks();
            } catch (TaskStorageException exception) {
                tasks.add(taskIndex, deletedTask);
                throw exception;
            } catch (RuntimeException exception) {
                tasks.add(taskIndex, deletedTask);
                throw exception;
            }

            UI.showTaskDeleted(deletedTask, tasks.size());
        } catch (NumberFormatException exception) {
            UI.showInvalidTaskNumber();
        }
    }

    /** Displays all stored tasks and their completion status. */
    private static void printTasks(String listOption) throws UnknownCommandException {
        boolean sortByTime;
        if (listOption.isEmpty()) {
            sortByTime = false;
        } else if ("time".equals(listOption)) {
            sortByTime = true;
        } else {
            throw new UnknownCommandException();
        }

        List<Task> tasksToDisplay = tasks.toList();
        if (sortByTime) {
            Comparator<LocalDateTime> dateTimeComparator =
                    Comparator.nullsLast(Comparator.naturalOrder());
            tasksToDisplay.sort(Comparator.comparing(
                    task -> task.getSortDateTime().orElse(null), dateTimeComparator));
        }

        UI.showTasks(tasksToDisplay);
    }
}
