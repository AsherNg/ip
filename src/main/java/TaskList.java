import java.util.ArrayList;
import java.util.List;

/**
 * Stores the tasks currently managed by CharlieK.
 *
 * <p>This class owns the in-memory collection so the rest of the application
 * does not need to depend directly on an {@link ArrayList}.</p>
 */
public class TaskList {
    /** The tasks in their normal insertion order. */
    private final ArrayList<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing a copy of the supplied tasks.
     *
     * @param initialTasks tasks to store initially
     */
    public TaskList(List<Task> initialTasks) {
        if (initialTasks == null) {
            throw new IllegalArgumentException("The initial task list cannot be null.");
        }
        tasks = new ArrayList<>(initialTasks);
    }

    /**
     * Adds a task to the end of this list.
     *
     * @param task the task to add
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Adds a task at a particular position.
     *
     * @param index the zero-based insertion position
     * @param task the task to add
     */
    public void add(int index, Task task) {
        tasks.add(index, task);
    }

    /**
     * Returns the task at a particular position.
     *
     * @param index the zero-based task position
     * @return the task at that position
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Removes and returns the task at a particular position.
     *
     * @param index the zero-based task position
     * @return the removed task
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Replaces the current contents with a copy of the supplied tasks.
     *
     * @param replacementTasks tasks to store
     */
    public void replaceWith(List<Task> replacementTasks) {
        if (replacementTasks == null) {
            throw new IllegalArgumentException("The replacement task list cannot be null.");
        }
        tasks.clear();
        tasks.addAll(replacementTasks);
    }

    /**
     * Returns the number of tasks in this list.
     *
     * @return the number of stored tasks
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns a snapshot of the current tasks for read-only processing.
     *
     * @return a copy of the tasks in insertion order
     */
    public List<Task> toList() {
        return new ArrayList<>(tasks);
    }
}
