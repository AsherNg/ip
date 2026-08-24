import java.time.DateTimeException;

/**
 * Interprets complete lines entered by the user.
 *
 * <p>The parser identifies the command and separates its argument from the
 * command keyword. It also validates task arguments and creates task objects;
 * it does not execute commands or persist tasks.</p>
 */
public class Parser {
    /**
     * Parses one complete user input line.
     *
     * @param input the line entered by the user
     * @return the identified command and its argument
     * @throws UnknownCommandException when the input does not start with a known command
     */
    public ParsedCommand parse(String input) throws UnknownCommandException {
        Command command = Command.fromInput(input)
                .orElseThrow(UnknownCommandException::new);
        return new ParsedCommand(command, command.argumentFrom(input));
    }

    /**
     * Parses a to-do description into a task.
     *
     * @param command the argument following the {@code todo} keyword
     * @return the parsed to-do task
     * @throws EmptyTaskDescriptionException when the description is blank
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
     * @param command the argument following the {@code deadline} keyword
     * @return the parsed deadline task
     * @throws EmptyTaskDescriptionException when the description is blank
     * @throws EmptyParameterException when the {@code /by} parameter is missing or blank
     * @throws InvalidDateTimeException when the deadline has an unsupported format
     */
    public Deadline parseDeadline(String command)
            throws EmptyTaskDescriptionException, EmptyParameterException,
            InvalidDateTimeException {
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
            return new Deadline(description, DateTimeParser.parseUserInput(deadline));
        } catch (DateTimeException exception) {
            throw new InvalidDateTimeException();
        }
    }

    /**
     * Parses an event description and its start/end date-times into a task.
     *
     * @param command the argument following the {@code event} keyword
     * @return the parsed event task
     * @throws EmptyTaskDescriptionException when the description is blank
     * @throws EmptyParameterException when an event parameter is missing or blank
     * @throws InvalidDateTimeException when either date/time has an unsupported format
     */
    public Event parseEvent(String command)
            throws EmptyTaskDescriptionException, EmptyParameterException,
            InvalidDateTimeException {
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
            return new Event(description,
                    DateTimeParser.parseUserInput(from), DateTimeParser.parseUserInput(to));
        } catch (DateTimeException exception) {
            throw new InvalidDateTimeException();
        }
    }

    /** Represents the result of parsing one user input line. */
    public static class ParsedCommand {
        /** The command identified in the input. */
        private final Command command;

        /** The text following the command keyword. */
        private final String argument;

        /**
         * Creates a parsed command result.
         *
         * @param command the identified command
         * @param argument the text following the command keyword
         */
        public ParsedCommand(Command command, String argument) {
            this.command = command;
            this.argument = argument;
        }

        /**
         * Returns the identified command.
         *
         * @return the command
         */
        public Command command() {
            return command;
        }

        /**
         * Returns the text following the command keyword.
         *
         * @return the command argument, possibly empty
         */
        public String argument() {
            return argument;
        }
    }
}
