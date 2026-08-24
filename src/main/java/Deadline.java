import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * A task that must be completed before a specified date or time.
 */
public class Deadline extends Task {
    /** The date by which the task should be completed, when no time was supplied. */
    private final LocalDate deadlineDate;

    /** The date and time by which the task should be completed, when a time was supplied. */
    private final LocalDateTime deadlineDateTime;

    /**
     * Creates an incomplete deadline task.
     *
     * @param description the text describing the task
     * @param deadline the date or time by which the task should be completed
     */
    public Deadline(String description, String deadline) {
        this(description, DateTimeParser.parseUserInput(deadline));
    }

    /** Creates a deadline with a date-only value. */
    public Deadline(String description, LocalDate deadline) {
        this(description, DateTimeParser.ParsedDateTime.ofDate(deadline));
    }

    /** Creates a deadline with a date and time value. */
    public Deadline(String description, LocalDateTime deadline) {
        this(description, DateTimeParser.ParsedDateTime.ofDateTime(deadline));
    }

    /** Creates a deadline from a parsed date or date-time value. */
    public Deadline(String description, DateTimeParser.ParsedDateTime deadline) {
        super(description);
        this.deadlineDate = deadline.date();
        this.deadlineDateTime = deadline.dateTime();
    }

    /**
     * Returns the type of this task.
     *
     * @return {@link TaskType#DEADLINE}
     */
    @Override
    protected TaskType getType() {
        return TaskType.DEADLINE;
    }

    /**
     * Returns the values stored for this deadline after the common task fields.
     *
     * @return the description and deadline values
     */
    @Override
    public List<String> getStorageFields() {
        return List.of(description, DateTimeParser.formatForStorage(toParsedDateTime()));
    }

    /**
     * Returns the deadline in the task-list format.
     *
     * @return the formatted deadline
     */
    @Override
    protected String getDateDetails() {
        return " (by: " + DateTimeParser.formatForDisplay(toParsedDateTime()) + ")";
    }

    /** Returns this deadline as the date/time value used by the parser and formatter. */
    private DateTimeParser.ParsedDateTime toParsedDateTime() {
        return new DateTimeParser.ParsedDateTime(deadlineDate, deadlineDateTime);
    }
}
