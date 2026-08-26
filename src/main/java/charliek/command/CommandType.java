package charliek.command;

import java.util.Optional;

/**
 * Describes the command keywords recognized by the parser.
 */
public enum CommandType {
    /** Exits the application. */
    BYE("bye", false),

    /** Displays all tasks, optionally sorted by time. */
    LIST("list", true),

    /** Marks a task as done. */
    MARK("mark", true),

    /** Marks a task as not done. */
    UNMARK("unmark", true),

    /** Deletes a task. */
    DELETE("delete", true),

    /** Adds a to-do task. */
    TODO("todo", true),

    /** Adds a deadline task. */
    DEADLINE("deadline", true),

    /** Adds an event task. */
    EVENT("event", true),

    /** Finds tasks matching a keyword. */
    FIND("find", true);

    /** The text the user types for this command. */
    private final String keyword;

    /** Whether this command may be followed by an argument. */
    private final boolean acceptsArguments;

    /**
     * Creates a command definition.
     *
     * @param keyword the command keyword
     * @param acceptsArguments whether the command accepts an argument
     */
    CommandType(String keyword, boolean acceptsArguments) {
        this.keyword = keyword;
        this.acceptsArguments = acceptsArguments;
    }

    /**
     * Finds the command represented by a complete input line.
     *
     * @param input the input line entered by the user
     * @return the matching command, or an empty result for an unknown command
     */
    public static Optional<CommandType> fromInput(String input) {
        if (input == null) {
            return Optional.empty();
        }
        for (CommandType command : values()) {
            boolean isExactMatch = input.equals(command.keyword);
            boolean isArgumentMatch = command.acceptsArguments
                    && input.startsWith(command.keyword + " ");
            if (isExactMatch || isArgumentMatch) {
                return Optional.of(command);
            }
        }
        return Optional.empty();
    }

    /**
     * Extracts the text after this command's keyword.
     *
     * @param input the complete input line
     * @return the text after the keyword and its separating space
     */
    public String argumentFrom(String input) {
        if (input == null) {
            return "";
        }
        int argumentStart = keyword.length();
        if (input.length() > argumentStart && input.charAt(argumentStart) == ' ') {
            argumentStart++;
        }
        return input.substring(argumentStart);
    }
}

