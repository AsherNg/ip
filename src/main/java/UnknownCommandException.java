/**
 * Indicates that CharlieK does not recognize the user's command.
 */
public class UnknownCommandException extends CharlieKException {
    /** The message displayed when a command is not recognized. */
    private static final String MESSAGE =
            "I do not know what that command means, but I know how to carry the flame!";

    /** Creates an exception for an unrecognized command. */
    public UnknownCommandException() {
        super(MESSAGE);
    }
}
