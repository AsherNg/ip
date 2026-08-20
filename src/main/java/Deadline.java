/**
 * A task that must be completed before a specified date or time.
 */
public class Deadline extends Task {
    /** The date or time by which the task should be completed. */
    private final String deadline;

    /**
     * Creates an incomplete deadline task.
     *
     * @param description the text describing the task
     * @param deadline the date or time by which the task should be completed
     */
    public Deadline(String description, String deadline) {
        super(description);
        this.deadline = deadline;
    }

    /**
     * Returns the display icon for a deadline task.
     *
     * @return {@code D}
     */
    @Override
    protected String getTypeIcon() {
        return "D";
    }

    /**
     * Returns the deadline in the task-list format.
     *
     * @return the formatted deadline
     */
    @Override
    protected String getDateDetails() {
        return " (by: " + deadline + ")";
    }
}
