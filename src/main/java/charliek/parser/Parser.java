package charliek.parser;

import java.time.DateTimeException;
import java.util.Objects;

import charliek.command.AddCommand;
import charliek.command.Command;
import charliek.command.CommandType;
import charliek.command.DeleteCommand;
import charliek.command.ExitCommand;
import charliek.command.FindCommand;
import charliek.command.ListCommand;
import charliek.command.MarkCommand;
import charliek.command.UnmarkCommand;
import charliek.exception.CharlieKException;
import charliek.exception.EmptyParameterException;
import charliek.exception.EmptyTaskDescriptionException;
import charliek.exception.InvalidDateTimeException;
import charliek.exception.UnknownCommandException;
import charliek.model.Deadline;
import charliek.model.Event;
import charliek.model.TaskList;
import charliek.model.ToDo;
import charliek.storage.Storage;
import charliek.ui.Ui;

/**
 * Interprets complete lines entered by the user.
 *
 * <p>The parser identifies the command and separates its argument from the
 * command keyword. It also validates task arguments and creates task objects;
 * it does not execute commands or persist tasks.</p>
 */
public class Parser {
    /**
     * Marker separating a deadline description from its date/time.
     */
    private static final String DEADLINE_MARKER = " /by ";

    /**
     * Marker separating an event description from its start date/time.
     */
    private static final String EVENT_START_MARKER = " /from ";

    /**
     * Marker separating an event start date/time from its end date/time.
     */
    private static final String EVENT_END_MARKER = " /to ";

    /**
     * The task list used by commands created by this parser.
     */
    private final TaskList tasks;

    /**
     * The UI used by commands created by this parser.
     */
    private final Ui ui;

    /**
     * The storage used by commands created by this parser.
     */
    private final Storage storage;

    /**
     * Creates a parser that can construct executable commands.
     *
     * @param tasks the task list commands will operate on.
     * @param ui the UI commands will use for output.
     * @param storage the storage commands will use for persistence.
     */
    public Parser(TaskList tasks, Ui ui, Storage storage) {
        this.tasks = Objects.requireNonNull(tasks);
        this.ui = Objects.requireNonNull(ui);
        this.storage = Objects.requireNonNull(storage);
    }

    /**
     * Parses one complete user input line.
     *
     * @param input the line entered by the user.
     * @return an executable command for the input.
     * @throws CharlieKException when the command or its arguments are invalid.
     */
    public Command parse(String input) throws CharlieKException {
        CommandType command = CommandType.getCommandFromInput(input)
                .orElseThrow(UnknownCommandException::new);
        String argument = command.getArgumentFromInput(input);
        return switch (command) {
            case BYE -> new ExitCommand(ui);
            case LIST -> new ListCommand(tasks, ui, argument.trim());
            case MARK -> new MarkCommand(tasks, ui, storage, argument);
            case UNMARK -> new UnmarkCommand(tasks, ui, storage, argument);
            case FIND -> new FindCommand(tasks, ui, argument);
            case DELETE -> new DeleteCommand(tasks, ui, storage, argument);
            case TODO -> new AddCommand(tasks, ui, storage, parseToDo(argument));
            case DEADLINE -> new AddCommand(tasks, ui, storage, parseDeadline(argument));
            case EVENT -> new AddCommand(tasks, ui, storage, parseEvent(argument));
        };
    }

    /**
     * Parses a to-do description into a task.
     *
     * @param command the argument following the {@code todo} keyword.
     * @return the parsed to-do task.
     * @throws EmptyTaskDescriptionException when the description is blank.
     */
    public ToDo parseToDo(String command) throws EmptyTaskDescriptionException {
        String description = command.trim();
        if (description.isEmpty()) {
            throw new EmptyTaskDescriptionException();
        }
        return new ToDo(description);
    }

    /**
     * Parses a deadline description and date/time into a task.
     *
     * @param command the argument following the {@code deadline} keyword.
     * @return the parsed deadline task.
     * @throws EmptyTaskDescriptionException when the description is blank.
     * @throws EmptyParameterException when the {@code /by} parameter is missing or blank.
     * @throws InvalidDateTimeException when the deadline has an unsupported format.
     */
    public Deadline parseDeadline(String command)
            throws EmptyTaskDescriptionException, EmptyParameterException,
            InvalidDateTimeException {
        String commandText = command.trim();
        if (commandText.isEmpty()) {
            throw new EmptyTaskDescriptionException();
        }

        int markerIndex = commandText.indexOf(DEADLINE_MARKER);
        String description = markerIndex < 0
                ? commandText
                : commandText.substring(0, markerIndex).trim();
        if (description.isEmpty()) {
            throw new EmptyTaskDescriptionException();
        }
        if (markerIndex < 0) {
            throw new EmptyParameterException();
        }

        String deadline = commandText.substring(markerIndex + DEADLINE_MARKER.length()).trim();
        if (deadline.isEmpty()) {
            throw new EmptyParameterException();
        }

        try {
            return new Deadline(description, DateTimeParser.parseUserInput(deadline));
        } catch (DateTimeException exception) {
            throw new InvalidDateTimeException();
        }
    }

    /**
     * Parses an event description and its start/end date-times into a task.
     *
     * @param command the argument following the {@code event} keyword.
     * @return the parsed event task.
     * @throws EmptyTaskDescriptionException when the description is blank.
     * @throws EmptyParameterException when an event parameter is missing or blank.
     * @throws InvalidDateTimeException when either date/time has an unsupported format.
     */
    public Event parseEvent(String command)
            throws EmptyTaskDescriptionException, EmptyParameterException,
            InvalidDateTimeException {
        String commandText = command.trim();
        if (commandText.isEmpty()) {
            throw new EmptyTaskDescriptionException();
        }

        int fromMarkerIndex = commandText.indexOf(EVENT_START_MARKER);
        String description = fromMarkerIndex < 0
                ? commandText
                : commandText.substring(0, fromMarkerIndex).trim();
        if (description.isEmpty()) {
            throw new EmptyTaskDescriptionException();
        }

        int toMarkerIndex = commandText.indexOf(EVENT_END_MARKER, fromMarkerIndex + 1);
        if (fromMarkerIndex < 0 || toMarkerIndex < 0) {
            throw new EmptyParameterException();
        }

        int fromValueStart = fromMarkerIndex + EVENT_START_MARKER.length();
        if (toMarkerIndex <= fromValueStart) {
            throw new EmptyParameterException();
        }

        String from = commandText.substring(fromValueStart, toMarkerIndex).trim();
        String to = commandText.substring(toMarkerIndex + EVENT_END_MARKER.length()).trim();
        if (from.isEmpty() || to.isEmpty()) {
            throw new EmptyParameterException();
        }

        try {
            return new Event(description,
                    DateTimeParser.parseUserInput(from), DateTimeParser.parseUserInput(to));
        } catch (DateTimeException exception) {
            throw new InvalidDateTimeException();
        }
    }

}
