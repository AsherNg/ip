/**
 * Represents a task entered into CharlieK's task list.
 */
public abstract class Task {
    /** The text describing this task. */
    protected String description;

    /** Whether this task has been marked as done. */
    protected boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description the text describing the task
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the symbol used to display this task's completion status.
     *
     * @return {@code X} when done, otherwise a blank space
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns the type of this task.
     * Subclasses override this method to provide their own task type.
     *
     * @return this task's type
     */
    protected abstract TaskType getType();

    /**
     * Returns any additional information shown after the task description.
     *
     * @return additional task information, or an empty string
     */
    protected String getDateDetails() {
        return "";
    }

    /**
     * Checks whether this task has been marked as done.
     *
     * @return {@code true} when the task is done
     */
    public boolean isDone() {
        return isDone;
    }

    /** Marks this task as done. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as not done. */
    public void markAsNotDone() {
        isDone = false;
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
