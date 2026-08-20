/**
 * A task that has no date or time attached to it.
 */
public class ToDo extends Task {
    /**
     * Creates an incomplete to-do task.
     *
     * @param description the text describing the task
     */
    public ToDo(String description) {
        super(description);
    }

    /**
     * Returns the display icon for a to-do task.
     *
     * @return {@code T}
     */
    @Override
    protected String getTypeIcon() {
        return "T";
    }
}
