/**
 * Deletes a selected task and persists the updated list.
 */
public class DeleteCommand extends Command {
    /** The one-based task number supplied by the user. */
    private final String taskNumberText;

    /**
     * Creates a delete command.
     *
     * @param taskNumberText the task number supplied by the user
     */
    public DeleteCommand(String taskNumberText) {
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
            Task deletedTask = tasks.remove(taskIndex);
            try {
                storage.save(tasks.toList());
            } catch (TaskStorageException exception) {
                tasks.add(taskIndex, deletedTask);
                throw exception;
            } catch (RuntimeException exception) {
                tasks.add(taskIndex, deletedTask);
                throw exception;
            }

            ui.showTaskDeleted(deletedTask, tasks.size());
        } catch (NumberFormatException exception) {
            ui.showInvalidTaskNumber();
        }
    }
}
