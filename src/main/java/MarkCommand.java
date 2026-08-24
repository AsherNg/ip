/**
 * Marks a selected task as done and persists the updated list.
 */
public class MarkCommand extends Command {
    /** The task list to update. */
    private final TaskList tasks;

    /** The UI used to show the result. */
    private final Ui ui;

    /** The storage used to persist the updated list. */
    private final Storage storage;

    /** The one-based task number supplied by the user. */
    private final String taskNumberText;

    /**
     * Creates a mark command.
     *
     * @param tasks the task list to update
     * @param ui the UI used to show the result
     * @param storage the storage used to persist the updated list
     * @param taskNumberText the task number supplied by the user
     */
    public MarkCommand(TaskList tasks, Ui ui, Storage storage, String taskNumberText) {
        this.tasks = tasks;
        this.ui = ui;
        this.storage = storage;
        this.taskNumberText = taskNumberText;
    }

    @Override
    public void execute() throws TaskStorageException {
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
                storage.save(tasks.toList());
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
}
