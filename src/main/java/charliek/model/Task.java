package charliek.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Represents a task entered into CharlieK's task list.
 */
public abstract class Task {
    /** The immutable text describing this task. */
    private final String description;

    /** Whether this task has been marked as done. */
    private boolean done;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description the text describing the task
     */
    public Task(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("A task description must not be blank.");
        }
        this.description = description;
        done = false;
    }

    /**
     * Returns this task description.
     *
     * @return the task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the symbol used to display this task's completion status.
     *
     * @return {@code X} when done, otherwise a blank space
     */
    public String getStatusIcon() {
        return done ? "X" : " ";
    }

    /**
     * Returns the type of this task.
     * Subclasses override this method to provide their own task type.
     *
     * @return this task's type
     */
    protected abstract TaskType getType();

    /**
     * Returns this task's type marker used in the CSV file.
     *
     * @return the one-letter task type marker
     */
    public String getStorageType() {
        return getType().getIcon();
    }

    /**
     * Returns the task values stored after the type and completion columns.
     * Subclasses add their type-specific parameters.
     *
     * @return this task's CSV data values
     */
    public List<String> getStorageFields() {
        return List.of(description);
    }

    /**
     * Returns any additional information shown after the task description.
     *
     * @return additional task information, or an empty string
     */
    protected String getDateDetails() {
        return "";
    }

    /**
     * Returns the date/time used to order this task in a chronological list.
     * Undated tasks return an empty value and are placed after dated tasks.
     *
     * @return this task's chronological ordering key, when it has one
     */
    public Optional<LocalDateTime> getSortDateTime() {
        return Optional.empty();
    }

    /**
     * Checks whether this task has been marked as done.
     *
     * @return {@code true} when the task is done
     */
    public boolean isDone() {
        return done;
    }

    /** Marks this task as done. */
    public void markAsDone() {
        done = true;
    }

    /** Marks this task as not done. */
    public void markAsNotDone() {
        done = false;
    }

    /**
     * Returns the task in the format used by CharlieK's output.
     *
     * @return the status icon and task description
     */
    @Override
    public String toString() {
        return "[" + getType().getIcon() + "][" + getStatusIcon() + "] "
                + description + getDateDetails();
    }
}
