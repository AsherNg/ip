import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

/**
 * Runs the CharlieK command-line chatbot.
 */
public class CharlieK {
    /** Stores the tasks entered during this run of the program. */
    private final TaskList tasks;

    /** Provides the task list's file-system persistence. */
    private final Storage storage;

    /** Interprets command lines entered by the user. */
    private final Parser parser;

    /** Handles console input and common session messages. */
    private final Ui ui;

    /** Creates an application instance using the supplied task-file path. */
    public CharlieK(String filePath) {
        storage = new Storage(Path.of(filePath));
        tasks = new TaskList();
        parser = new Parser();
        ui = new Ui();
    }

    /** Starts the application session. */
    public void run() {
        String loadingError = null;
        try {
            loadTasks();
        } catch (TaskStorageException exception) {
            loadingError = exception.getMessage();
        } catch (RuntimeException exception) {
            loadingError = "I couldn't load saved tasks because the saved data is invalid.";
        }

        ui.showWelcome(loadingError);

        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            ui.showLine();

            try {
                Parser.ParsedCommand parsedCommand = parser.parse(command);
                Command commandType = parsedCommand.command();
                String argument = parsedCommand.argument();

                if (commandType == Command.BYE) {
                    ui.showGoodbye();
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
                    addTypedTask(parser.parseToDo(argument));
                    break;
                case DEADLINE:
                    addTypedTask(parser.parseDeadline(argument));
                    break;
                case EVENT:
                    addTypedTask(parser.parseEvent(argument));
                    break;
                default:
                    throw new UnknownCommandException();
                }
            } catch (CharlieKException exception) {
                ui.showError(exception.getMessage());
            } catch (RuntimeException exception) {
                ui.showProcessingError();
            }
            ui.showLine();
        }
    }

    public static void main(String[] args) {
        new CharlieK("data/charliek.csv").run();
    }

    /**
     * Adds a task to the in-memory task list.
     *
     * @param task the task object to store
     */
    private void addTask(Task task) throws TaskStorageException {
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
    private void saveTasks() throws TaskStorageException {
        storage.save(tasks.toList());
    }

    /**
     * Loads task lines saved by {@link #saveTasks()} when the application starts.
     * Missing files represent a new, empty task list.
     */
    private void loadTasks() throws TaskStorageException {
        tasks.replaceWith(storage.load());
    }

    /** Adds a typed task and prints the confirmation shown by the user interface. */
    private void addTypedTask(Task task) throws TaskStorageException {
        addTask(task);
        ui.showTaskAdded(task, tasks.size());
    }

    /**
     * Marks a task as done using its one-based position in the task list.
     *
     * @param taskNumberText the task number supplied after the {@code mark} command
     */
    private void markTask(String taskNumberText) throws TaskStorageException {
        try {
            int taskNumber = Integer.parseInt(taskNumberText);
            if (taskNumber < 1 || taskNumber > tasks.size()) {
                ui.showTaskDoesNotExist();
                return;
            }

            int taskIndex = taskNumber - 1;
            Task task = tasks.get(taskIndex);
            if (task.isDone()) {
                ui.showTaskAlreadyMarked(task);
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
            ui.showTaskMarked(task);
        } catch (NumberFormatException exception) {
            ui.showInvalidTaskNumber();
        }
    }

    /**
     * Marks a task as not done using its one-based position in the task list.
     *
     * @param taskNumberText the task number supplied after the {@code unmark} command
     */
    private void unmarkTask(String taskNumberText) throws TaskStorageException {
        try {
            int taskNumber = Integer.parseInt(taskNumberText);
            if (taskNumber < 1 || taskNumber > tasks.size()) {
                ui.showTaskDoesNotExist();
                return;
            }

            int taskIndex = taskNumber - 1;
            Task task = tasks.get(taskIndex);
            if (!task.isDone()) {
                ui.showTaskAlreadyUnmarked(task);
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
            ui.showTaskUnmarked(task);
        } catch (NumberFormatException exception) {
            ui.showInvalidTaskNumber();
        }
    }

    /**
     * Deletes a task using its one-based position in the task list.
     *
     * @param taskNumberText the task number supplied after the {@code delete} command
     */
    private void deleteTask(String taskNumberText) throws TaskStorageException {
        try {
            int taskNumber = Integer.parseInt(taskNumberText);
            if (taskNumber < 1 || taskNumber > tasks.size()) {
                ui.showTaskDoesNotExist();
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

            ui.showTaskDeleted(deletedTask, tasks.size());
        } catch (NumberFormatException exception) {
            ui.showInvalidTaskNumber();
        }
    }

    /** Displays all stored tasks and their completion status. */
    private void printTasks(String listOption) throws UnknownCommandException {
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

        ui.showTasks(tasksToDisplay);
    }
}
