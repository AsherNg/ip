/**
 * A task with a specified starting date or time and ending date or time.
 */
public class Event extends Task {
    /** The date or time at which the event starts. */
    private final String from;

    /** The date or time at which the event ends. */
    private final String to;

    /**
     * Creates an incomplete event task.
     *
     * @param description the text describing the event
     * @param from the date or time at which the event starts
     * @param to the date or time at which the event ends
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the display icon for an event task.
     *
     * @return {@code E}
     */
    @Override
    protected String getTypeIcon() {
        return "E";
    }

    /**
     * Returns the event's time range in the task-list format.
     *
     * @return the formatted event time range
     */
    @Override
    protected String getDateDetails() {
        return " (from: " + from + " to: " + to + ")";
    }
}
