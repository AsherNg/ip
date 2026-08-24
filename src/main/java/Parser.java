/**
 * Interprets complete lines entered by the user.
 *
 * <p>The parser identifies the command and separates its argument from the
 * command keyword. It does not execute the command or create tasks.</p>
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
