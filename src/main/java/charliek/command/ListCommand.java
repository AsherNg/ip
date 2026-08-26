package charliek.command;

import charliek.exception.UnknownCommandException;
import charliek.model.Task;
import charliek.model.TaskList;
import charliek.ui.Ui;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

/**
 * Displays the current tasks, optionally sorted by date and time.
 */
public class ListCommand extends Command {
    /** The task list to display. */
    private final TaskList tasks;

    /** The UI used to show the tasks. */
    private final Ui ui;

    /** The optional list mode supplied by the user. */
    private final String listOption;

    /**
     * Creates a list command.
     *
     * @param tasks the task list to display
     * @param ui the UI used to show the tasks
     * @param listOption an empty string or {@code time}
     */
    public ListCommand(TaskList tasks, Ui ui, String listOption) {
        this.tasks = tasks;
        this.ui = ui;
        this.listOption = listOption;
    }

    /**
     * Displays the tasks in insertion order or chronological order.
     *
     * @throws UnknownCommandException if the list option is not supported
     */
    @Override
    public void execute() throws UnknownCommandException {
        boolean sortByTime;
        if (listOption.isEmpty()) {
            sortByTime = false;
        } else if ("time".equals(listOption)) {
            sortByTime = true;
        } else {
            throw new UnknownCommandException();
        }

        List<Task> tasksToDisplay = tasks.toList();
        if (sortByTime) {
            Comparator<LocalDateTime> dateTimeComparator =
                    Comparator.nullsLast(Comparator.naturalOrder());
            tasksToDisplay.sort(Comparator.comparing(
                    task -> task.getSortDateTime().orElse(null), dateTimeComparator));
        }

        ui.showTasks(tasksToDisplay);
    }
}
