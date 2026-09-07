package charliek.command;

import charliek.exception.TaskStorageException;
import charliek.model.Task;
import charliek.model.TaskList;
import charliek.storage.Storage;
import charliek.ui.Ui;

/**
 * Marks a selected task as not done and persists the updated list.
 */
public class UnmarkCommand extends Command {
    /**
     * The task list to update.
     */
    private final TaskList tasks;

    /**
     * The UI used to show the result.
     */
    private final Ui ui;

    /**
     * The storage used to persist the updated list.
     */
    private final Storage storage;

    /**
     * The one-based task number supplied by the user.
     */
    private final String taskNumberText;

    /**
     * Creates an unmark command.
     *
     * @param tasks the task list to update.
     * @param ui the UI used to show the result.
     * @param storage the storage used to persist the updated list.
     * @param taskNumberText the task number supplied by the user.
     */
    public UnmarkCommand(TaskList tasks, Ui ui, Storage storage, String taskNumberText) {
        this.tasks = tasks;
        this.ui = ui;
        this.storage = storage;
        this.taskNumberText = taskNumberText;
    }

    /**
     * Marks the selected task as not done, saves the updated list, and reports the result.
     *
     * @throws TaskStorageException if the updated task list cannot be saved.
     */
    @Override
    public void execute() throws TaskStorageException {
        try {
            int taskNumber = Integer.parseInt(taskNumberText);
            if (taskNumber < 1 || taskNumber > tasks.size()) {
                ui.showTaskDoesNotExist();
                return;
            }

            int taskIndex = taskNumber - 1;
            // The range check above is the internal precondition for this zero-based lookup.
            assert taskIndex >= 0 && taskIndex < tasks.size()
                    : "A validated task number must produce a valid task index.";
            Task task = tasks.get(taskIndex);
            assert task != null : "A valid task index must contain a task.";
            if (!task.isDone()) {
                ui.showTaskAlreadyUnmarked(task);
                return;
            }

            task.markAsNotDone();
            assert !task.isDone() : "markAsNotDone must make the task incomplete.";
            try {
                storage.save(tasks.toList());
            } catch (TaskStorageException exception) {
                task.markAsDone();
                // A failed save must undo the in-memory status change.
                assert task.isDone() : "Failed unmark must restore the complete status.";
                throw exception;
            } catch (RuntimeException exception) {
                task.markAsDone();
                assert task.isDone() : "Failed unmark must restore the complete status.";
                throw exception;
            }
            ui.showTaskUnmarked(task);
        } catch (NumberFormatException exception) {
            ui.showInvalidTaskNumber();
        }
    }
}
