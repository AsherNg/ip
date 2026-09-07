package charliek.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores the tasks currently managed by CharlieK.
 *
 * <p>This class owns the in-memory collection so the rest of the application
 * does not need to depend directly on its list implementation.</p>
 */
public class TaskList {
    /**
     * The tasks in their normal insertion order.
     */
    private final List<Task> tasks;

    /**
     * Creates a task list containing copies of the supplied task references in their given order.
     *
     * <p>The varargs form keeps the common empty-list and small-list cases concise while still
     * allowing callers to pass an existing task array.</p>
     *
     * @param initialTasks tasks to store initially, or no tasks for an empty list.
     * @throws IllegalArgumentException if the task array or one of its elements is {@code null}.
     */
    public TaskList(Task... initialTasks) {
        if (initialTasks == null) {
            throw new IllegalArgumentException("The initial task list cannot be null.");
        }
        tasks = new ArrayList<>();
        for (Task task : initialTasks) {
            add(task);
        }
    }

    /**
     * Adds a task to the end of this list.
     *
     * @param task the task to add.
     * @throws IllegalArgumentException if {@code task} is {@code null}.
     */
    public void add(Task task) {
        validateTask(task);
        tasks.add(task);
    }

    /**
     * Adds a task at a particular position.
     *
     * @param index the zero-based insertion position.
     * @param task the task to add.
     * @throws IllegalArgumentException if {@code task} is {@code null}.
     */
    public void add(int index, Task task) {
        validateTask(task);
        tasks.add(index, task);
    }

    /**
     * Returns the task at a particular position.
     *
     * @param index the zero-based task position.
     * @return the task at that position.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Removes and returns the task at a particular position.
     *
     * @param index the zero-based task position.
     * @return the removed task.
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Replaces the current contents with a copy of the supplied tasks.
     *
     * @param replacementTasks tasks to store.
     * @throws IllegalArgumentException if the list or one of its elements is {@code null}.
     */
    public void replaceWith(List<Task> replacementTasks) {
        if (replacementTasks == null) {
            throw new IllegalArgumentException("The replacement task list cannot be null.");
        }
        for (Task task : replacementTasks) {
            validateTask(task);
        }
        tasks.clear();
        tasks.addAll(replacementTasks);
    }

    /**
     * Returns the number of tasks in this list.
     *
     * @return the number of stored tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns a snapshot of the current tasks for read-only processing.
     *
     * @return a copy of the tasks in insertion order.
     */
    public List<Task> toList() {
        return new ArrayList<>(tasks);
    }

    /**
     * Rejects null task references before they enter the list.
     */
    private static void validateTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("The task list cannot contain null tasks.");
        }
    }
}
