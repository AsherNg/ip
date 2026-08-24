package charliek.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Tests task-list storage, ordering, replacement, and snapshot behavior. */
class TaskListTest {
    /** Verifies that a new list is empty. */
    @Test
    void taskList_newList_isEmpty() {
        TaskList tasks = new TaskList();

        assertEquals(0, tasks.size());
        assertEquals(List.of(), tasks.toList());
    }

    /** Verifies that an initial collection is copied in its original order. */
    @Test
    void taskList_initialTasks_copiesInputOrder() {
        ArrayList<Task> initialTasks = new ArrayList<>(List.of(
                new ToDo("first"), new ToDo("second")));
        TaskList tasks = new TaskList(initialTasks);

        initialTasks.clear();

        assertEquals(2, tasks.size());
        assertEquals("first", tasks.get(0).getStorageFields().get(0));
        assertEquals("second", tasks.get(1).getStorageFields().get(0));
    }

    /** Verifies that null task-list inputs are rejected. */
    @Test
    void taskList_nullInput_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new TaskList(null));

        TaskList tasks = new TaskList();
        assertThrows(IllegalArgumentException.class, () -> tasks.replaceWith(null));
    }

    /** Verifies append and indexed insertion behavior. */
    @Test
    void taskList_add_appendAndIndexedInsertion_preservesExpectedOrder() {
        TaskList tasks = new TaskList();
        Task first = new ToDo("first");
        Task second = new ToDo("second");
        Task inserted = new ToDo("inserted");

        tasks.add(first);
        tasks.add(second);
        tasks.add(1, inserted);

        assertEquals(List.of(first, inserted, second), tasks.toList());
    }

    /** Verifies indexed access and removal return the expected task. */
    @Test
    void taskList_getAndRemove_validIndexReturnsTask() {
        Task first = new ToDo("first");
        Task second = new ToDo("second");
        TaskList tasks = new TaskList(List.of(first, second));

        assertEquals(first, tasks.get(0));
        assertEquals(first, tasks.remove(0));
        assertEquals(1, tasks.size());
        assertEquals(second, tasks.get(0));
    }

    /** Verifies replacement discards old tasks and copies the replacement list. */
    @Test
    void taskList_replaceWith_replacesContentsWithoutAliasingInput() {
        TaskList tasks = new TaskList(List.of(new ToDo("old")));
        ArrayList<Task> replacement = new ArrayList<>(List.of(new ToDo("new")));

        tasks.replaceWith(replacement);
        replacement.clear();

        assertEquals(1, tasks.size());
        assertEquals("new", tasks.get(0).getStorageFields().get(0));
    }

    /** Verifies that callers receive a modifiable snapshot, not the backing list. */
    @Test
    void taskList_toList_returnsIndependentSnapshot() {
        Task task = new ToDo("read book");
        TaskList tasks = new TaskList(List.of(task));

        List<Task> snapshot = tasks.toList();
        snapshot.clear();

        assertNotSame(snapshot, tasks.toList());
        assertEquals(1, tasks.size());
    }
}
