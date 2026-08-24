package charliek.command;

import charliek.CharlieK;
import charliek.exception.CharlieKException;

/**
 * Represents an executable command in the application.
 *
 * <p>Concrete commands receive the application collaborators they need so
 * that command-specific behavior can be moved out of {@link CharlieK}.</p>
 */
public abstract class Command {
    /** Executes this command. */
    public abstract void execute() throws CharlieKException;

    /**
     * Indicates whether this command ends the application session.
     *
     * @return {@code true} when the command requests application exit
     */
    public boolean isExit() {
        return false;
    }
}
