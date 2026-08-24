import java.nio.file.Path;

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
                Command executableCommand = createCommand(parsedCommand);
                executableCommand.execute(tasks, ui, storage);
                if (executableCommand.isExit()) {
                    break;
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
     * Creates an executable command from the parser's keyword and argument.
     *
     * @param parsedCommand the parser result for one user input line
     * @return the executable command
     * @throws CharlieKException when a task argument cannot be parsed
     */
    private Command createCommand(Parser.ParsedCommand parsedCommand)
            throws CharlieKException {
        CommandType commandType = parsedCommand.command();
        String argument = parsedCommand.argument();
        return switch (commandType) {
        case BYE -> new ExitCommand();
        case LIST -> new ListCommand(argument.trim());
        case MARK -> new MarkCommand(argument);
        case UNMARK -> new UnmarkCommand(argument);
        case DELETE -> new DeleteCommand(argument);
        case TODO -> new AddCommand(parser.parseToDo(argument));
        case DEADLINE -> new AddCommand(parser.parseDeadline(argument));
        case EVENT -> new AddCommand(parser.parseEvent(argument));
        };
    }

    /**
     * Loads task lines saved by the storage component when the application starts.
     * Missing files represent a new, empty task list.
     */
    private void loadTasks() throws TaskStorageException {
        tasks.replaceWith(storage.load());
    }
}
