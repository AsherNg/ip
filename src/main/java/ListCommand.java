import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

/**
 * Displays the current tasks, optionally sorted by date and time.
 */
public class ListCommand extends Command {
    /** The optional list mode supplied by the user. */
    private final String listOption;

    /**
     * Creates a list command.
     *
     * @param listOption an empty string or {@code time}
     */
    public ListCommand(String listOption) {
        this.listOption = listOption;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws UnknownCommandException {
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
