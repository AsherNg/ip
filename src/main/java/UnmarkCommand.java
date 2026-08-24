/**
 * Marks a selected task as not done and persists the updated list.
 */
public class UnmarkCommand extends Command {
    /** The one-based task number supplied by the user. */
    private final String taskNumberText;

    /**
     * Creates an unmark command.
     *
     * @param taskNumberText the task number supplied by the user
     */
    public UnmarkCommand(String taskNumberText) {
        this.taskNumberText = taskNumberText;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws TaskStorageException {
        try {
            int taskNumber = Integer.parseInt(taskNumberText);
            if (taskNumber < 1 || taskNumber > tasks.size()) {
                ui.showTaskDoesNotExist();
                return;
            }

            int taskIndex = taskNumber - 1;
            Task task = tasks.get(taskIndex);
            if (!task.isDone()) {
                ui.showTaskAlreadyUnmarked(task);
                return;
            }

            task.markAsNotDone();
            try {
                storage.save(tasks.toList());
            } catch (TaskStorageException exception) {
                task.markAsDone();
                throw exception;
            } catch (RuntimeException exception) {
                task.markAsDone();
                throw exception;
            }
            ui.showTaskUnmarked(task);
        } catch (NumberFormatException exception) {
            ui.showInvalidTaskNumber();
        }
    }
}
