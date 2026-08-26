package charliek.model;

import charliek.parser.DateTimeParser;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * A task with a specified starting date or time and ending date or time.
 */
public class Event extends Task {
    /** The event start date, when no start time was supplied. */
    private final LocalDate fromDate;

    /** The event start date and time, when a start time was supplied. */
    private final LocalDateTime fromDateTime;

    /** The event end date, when no end time was supplied. */
    private final LocalDate toDate;

    /** The event end date and time, when an end time was supplied. */
    private final LocalDateTime toDateTime;

    /**
     * Creates an incomplete event task.
     *
     * @param description the text describing the event
     * @param from the date or time at which the event starts
     * @param to the date or time at which the event ends
     */
    public Event(String description, String from, String to) {
        this(description, DateTimeParser.parseUserInput(from), DateTimeParser.parseUserInput(to));
    }

    /**
     * Creates an event from date-only or date-time values.
     *
     * @param description the text describing the event
     * @param from the parsed date or date-time at which the event starts
     * @param to the parsed date or date-time at which the event ends
     * @throws IllegalArgumentException if either date/time is {@code null}
     */
    public Event(String description, DateTimeParser.ParsedDateTime from,
            DateTimeParser.ParsedDateTime to) {
        super(description);
        if (from == null || to == null) {
            throw new IllegalArgumentException("Both event date/times are required.");
        }
        this.fromDate = from.date();
        this.fromDateTime = from.dateTime();
        this.toDate = to.date();
        this.toDateTime = to.dateTime();
    }

    /**
     * Returns the type of this task.
     *
     * @return {@link TaskType#EVENT}
     */
    @Override
    protected TaskType getType() {
        return TaskType.EVENT;
    }

    /**
     * Returns the values stored for this event after the common task fields.
     *
     * @return the description, starting time, and ending time values
     */
    @Override
    public List<String> getStorageFields() {
        return List.of(description,
                DateTimeParser.formatForStorage(new DateTimeParser.ParsedDateTime(fromDate, fromDateTime)),
                DateTimeParser.formatForStorage(new DateTimeParser.ParsedDateTime(toDate, toDateTime)));
    }

    /**
     * Returns the event's time range in the task-list format.
     *
     * @return the formatted event time range
     */
    @Override
    protected String getDateDetails() {
        return " (from: " + format(fromDate, fromDateTime)
                + " to: " + format(toDate, toDateTime) + ")";
    }

    /** Returns the event start as the task's chronological ordering key. */
    @Override
    public Optional<LocalDateTime> getSortDateTime() {
        LocalDateTime sortDateTime = fromDateTime == null
                ? fromDate.atStartOfDay()
                : fromDateTime;
        return Optional.of(sortDateTime);
    }

    /** Formats one endpoint of the event's time range for display. */
    private String format(LocalDate date, LocalDateTime dateTime) {
        return DateTimeParser.formatForDisplay(new DateTimeParser.ParsedDateTime(date, dateTime));
    }
}
