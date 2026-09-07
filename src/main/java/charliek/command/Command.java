package charliek.command;

import charliek.CharlieK;
import charliek.exception.CharlieKException;
import charliek.exception.TaskStorageException;
import charliek.model.TaskList;
import charliek.storage.Storage;

/**
 * Represents an executable command in the application.
 *
 * <p>Concrete commands receive the application collaborators they need so
 * that command-specific behavior can be moved out of {@link CharlieK}.</p>
 */
public abstract class Command {
    /**
     * Creates a command base instance for use by a concrete command.
     */
    protected Command() {
    }

    /**
     * Executes this command.
     *
     * @throws CharlieKException if the command cannot complete normally.
     */
    public abstract void execute() throws CharlieKException;

    /**
     * Indicates whether this command ends the application session.
     *
     * @return {@code true} when the command requests application exit.
     */
    public boolean isExit() {
        return false;
    }

    /**
     * Saves the current tasks and restores the prior state if saving fails.
     *
     * <p>Commands mutate the shared task list before persistence so their
     * success messages can reflect the new state. A rollback keeps memory and
     * disk consistent when persistence cannot complete.</p>
     *
     * @param storage the storage service used to persist the task list.
     * @param tasks the task list to save.
     * @param rollback restores the task list state from before the mutation.
     * @throws TaskStorageException if the task list cannot be saved.
     */
    protected final void saveTasksOrRollback(Storage storage, TaskList tasks, Runnable rollback)
            throws TaskStorageException {
        try {
            storage.save(tasks.toList());
        } catch (TaskStorageException | RuntimeException exception) {
            rollback.run();
            throw exception;
        }
    }
}
