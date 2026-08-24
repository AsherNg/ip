/**
 * Adds a parsed task to the task list and persists the updated list.
 */
public class AddCommand extends Command {
    /** The task to add. */
    private final Task task;

    /**
     * Creates an add command for a parsed task.
     *
     * @param task the task to add
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws TaskStorageException {
        tasks.add(task);
        try {
            storage.save(tasks.toList());
        } catch (TaskStorageException exception) {
            tasks.remove(tasks.size() - 1);
            throw exception;
        } catch (RuntimeException exception) {
            tasks.remove(tasks.size() - 1);
            throw exception;
        }
        ui.showTaskAdded(task, tasks.size());
    }
}
