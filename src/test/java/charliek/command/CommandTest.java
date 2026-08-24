package charliek.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import charliek.exception.TaskStorageException;
import charliek.exception.UnknownCommandException;
import charliek.model.Deadline;
import charliek.model.Event;
import charliek.model.Task;
import charliek.model.TaskList;
import charliek.model.ToDo;
import charliek.storage.Storage;
import charliek.ui.Ui;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests command execution, persistence, validation, and rollback behavior. */
class CommandTest {
    @TempDir
    Path tempDirectory;

    private InputStream originalInput;
    private PrintStream originalOutput;
    private ByteArrayOutputStream capturedOutput;

    /** Redirects console dependencies so command tests remain isolated and readable. */
    @BeforeEach
    void setUpConsole() {
        originalInput = System.in;
        originalOutput = System.out;
        capturedOutput = new ByteArrayOutputStream();
        System.setIn(new ByteArrayInputStream(new byte[0]));
        System.setOut(new PrintStream(capturedOutput));
    }

    /** Restores the process-wide console streams after each command test. */
    @AfterEach
    void restoreConsole() {
        System.setIn(originalInput);
        System.setOut(originalOutput);
    }

    /** Verifies that adding a task updates memory and persists the task. */
    @Test
    void addCommand_execute_success_addsAndSavesTask() throws Exception {
        TaskList tasks = new TaskList();
        Storage storage = storageAt("tasks.csv");
        Task task = new ToDo("read book");

        new AddCommand(tasks, new Ui(), storage, task).execute();

        assertEquals(1, tasks.size());
        assertEquals(task, tasks.get(0));
        assertEquals(task.toString(), storage.load().get(0).toString());
        assertTrue(capturedOutput.toString().contains("I've added this task"));
    }

    /** Verifies that a failed add save rolls the in-memory list back. */
    @Test
    void addCommand_saveFails_removesTaskFromList() throws IOException {
        TaskList tasks = new TaskList();
        Path taskPath = tempDirectory.resolve("tasks.csv");
        Files.createDirectory(taskPath);

        assertThrows(TaskStorageException.class,
                () -> new AddCommand(tasks, new Ui(), new Storage(taskPath),
                        new ToDo("read book")).execute());

        assertEquals(0, tasks.size());
    }

    /** Verifies marking an incomplete task updates its status and persists it. */
    @Test
    void markCommand_executeOnIncompleteTask_marksAndSavesTask() throws Exception {
        Task task = new ToDo("read book");
        TaskList tasks = new TaskList(List.of(task));
        Storage storage = storageAt("tasks.csv");

        new MarkCommand(tasks, new Ui(), storage, "1").execute();

        assertTrue(task.isDone());
        assertTrue(storage.load().get(0).isDone());
    }

    /** Verifies that already marked, invalid, and out-of-range requests are harmless. */
    @Test
    void markCommand_invalidOrAlreadyMarkedTask_leavesStateUnchanged() throws Exception {
        Task task = new ToDo("read book");
        task.markAsDone();
        TaskList tasks = new TaskList(List.of(task));
        Storage storage = storageAt("tasks.csv");

        new MarkCommand(tasks, new Ui(), storage, "1").execute();
        new MarkCommand(tasks, new Ui(), storage, "0").execute();
        new MarkCommand(tasks, new Ui(), storage, "not-a-number").execute();

        assertTrue(task.isDone());
        assertTrue(capturedOutput.toString().contains("already marked"));
        assertTrue(capturedOutput.toString().contains("valid task number"));
    }

    /** Verifies that a failed mark save restores the incomplete status. */
    @Test
    void markCommand_saveFails_restoresIncompleteStatus() throws IOException {
        Task task = new ToDo("read book");
        TaskList tasks = new TaskList(List.of(task));
        Path taskPath = tempDirectory.resolve("tasks.csv");
        Files.createDirectory(taskPath);

        assertThrows(TaskStorageException.class,
                () -> new MarkCommand(tasks, new Ui(), new Storage(taskPath), "1").execute());

        assertFalse(task.isDone());
    }

    /** Verifies unmarking a completed task updates its status and persists it. */
    @Test
    void unmarkCommand_executeOnCompletedTask_unmarksAndSavesTask() throws Exception {
        Task task = new ToDo("read book");
        task.markAsDone();
        TaskList tasks = new TaskList(List.of(task));
        Storage storage = storageAt("tasks.csv");

        new UnmarkCommand(tasks, new Ui(), storage, "1").execute();

        assertFalse(task.isDone());
        assertFalse(storage.load().get(0).isDone());
    }

    /** Verifies that already unmarked and invalid requests are harmless. */
    @Test
    void unmarkCommand_invalidOrAlreadyUnmarkedTask_leavesStateUnchanged() throws Exception {
        Task task = new ToDo("read book");
        TaskList tasks = new TaskList(List.of(task));
        Storage storage = storageAt("tasks.csv");

        new UnmarkCommand(tasks, new Ui(), storage, "1").execute();
        new UnmarkCommand(tasks, new Ui(), storage, "2").execute();
        new UnmarkCommand(tasks, new Ui(), storage, "not-a-number").execute();

        assertFalse(task.isDone());
        assertTrue(capturedOutput.toString().contains("already unmarked"));
        assertTrue(capturedOutput.toString().contains("valid task number"));
    }

    /** Verifies that a failed unmark save restores the completed status. */
    @Test
    void unmarkCommand_saveFails_restoresCompletedStatus() throws IOException {
        Task task = new ToDo("read book");
        task.markAsDone();
        TaskList tasks = new TaskList(List.of(task));
        Path taskPath = tempDirectory.resolve("tasks.csv");
        Files.createDirectory(taskPath);

        assertThrows(TaskStorageException.class,
                () -> new UnmarkCommand(tasks, new Ui(), new Storage(taskPath), "1").execute());

        assertTrue(task.isDone());
    }

    /** Verifies that deleting a task removes and persists the selected task. */
    @Test
    void deleteCommand_executeOnValidTask_removesAndSavesTask() throws Exception {
        Task first = new ToDo("first");
        Task second = new ToDo("second");
        TaskList tasks = new TaskList(List.of(first, second));
        Storage storage = storageAt("tasks.csv");

        new DeleteCommand(tasks, new Ui(), storage, "1").execute();

        assertEquals(List.of(second), tasks.toList());
        assertEquals(List.of("second"), storage.load().get(0).getStorageFields());
    }

    /** Verifies that invalid delete requests do not change the task list. */
    @Test
    void deleteCommand_invalidTaskNumber_leavesListUnchanged() throws Exception {
        Task task = new ToDo("read book");
        TaskList tasks = new TaskList(List.of(task));
        Storage storage = storageAt("tasks.csv");

        new DeleteCommand(tasks, new Ui(), storage, "0").execute();
        new DeleteCommand(tasks, new Ui(), storage, "not-a-number").execute();
        new DeleteCommand(tasks, new Ui(), storage, "2").execute();

        assertEquals(List.of(task), tasks.toList());
    }

    /** Verifies that a failed delete save restores the task at its original index. */
    @Test
    void deleteCommand_saveFails_restoresDeletedTask() throws IOException {
        Task first = new ToDo("first");
        Task second = new ToDo("second");
        TaskList tasks = new TaskList(List.of(first, second));
        Path taskPath = tempDirectory.resolve("tasks.csv");
        Files.createDirectory(taskPath);

        assertThrows(TaskStorageException.class,
                () -> new DeleteCommand(tasks, new Ui(), new Storage(taskPath), "2").execute());

        assertEquals(List.of(first, second), tasks.toList());
    }

    /** Verifies that ordinary listing preserves insertion order. */
    @Test
    void listCommand_withoutOption_displaysInsertionOrder() throws Exception {
        TaskList tasks = new TaskList(List.of(new ToDo("first"), new ToDo("second")));

        new ListCommand(tasks, new Ui(), "").execute();

        String output = capturedOutput.toString();
        assertTrue(output.indexOf("1.[T][ ] first") < output.indexOf("2.[T][ ] second"));
    }

    /** Verifies that time listing sorts dated tasks before undated tasks. */
    @Test
    void listCommand_timeOption_displaysChronologicalOrder() throws Exception {
        TaskList tasks = new TaskList(List.of(
                new ToDo("no date"),
                new Deadline("later", LocalDate.of(2026, 12, 31)),
                new Event("early", "2026-01-01", "2026-01-02")));

        new ListCommand(tasks, new Ui(), "time").execute();

        String output = capturedOutput.toString();
        assertTrue(output.indexOf("1.[E][ ] early") < output.indexOf("2.[D][ ] later"));
        assertTrue(output.indexOf("2.[D][ ] later") < output.indexOf("3.[T][ ] no date"));
    }

    /** Verifies that unsupported list modes are rejected. */
    @Test
    void listCommand_unsupportedOption_throwsUnknownCommandException() {
        assertThrows(UnknownCommandException.class,
                () -> new ListCommand(new TaskList(), new Ui(), "unsupported").execute());
    }

    /** Verifies that the exit command reports its session-ending state. */
    @Test
    void exitCommand_execute_marksCommandAsExit() {
        ExitCommand command = new ExitCommand(new Ui());

        command.execute();

        assertTrue(command.isExit());
        assertTrue(capturedOutput.toString().contains("Bye. Hope to see you again soon!"));
    }

    /** Creates storage at an isolated temporary path. */
    private Storage storageAt(String fileName) {
        return new Storage(tempDirectory.resolve(fileName));
    }
}
