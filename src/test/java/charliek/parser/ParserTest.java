package charliek.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import charliek.command.AddCommand;
import charliek.command.DeleteCommand;
import charliek.command.ExitCommand;
import charliek.command.FindCommand;
import charliek.command.HelpCommand;
import charliek.command.ListCommand;
import charliek.command.MarkCommand;
import charliek.command.UnmarkCommand;
import charliek.exception.EmptyParameterException;
import charliek.exception.EmptyTaskDescriptionException;
import charliek.exception.InvalidDateTimeException;
import charliek.exception.UnknownCommandException;
import charliek.model.Deadline;
import charliek.model.Event;
import charliek.model.TaskList;
import charliek.model.ToDo;
import charliek.storage.Storage;
import charliek.ui.Ui;

/**
 * Tests command parsing and validation before commands are executed.
 */
class ParserTest {
    @TempDir
    Path tempDirectory;

    private InputStream originalInput;
    private Parser parser;

    /**
     * Creates a parser with isolated task storage for each test.
     */
    @BeforeEach
    void setUp() {
        originalInput = System.in;
        System.setIn(new ByteArrayInputStream(new byte[0]));
        parser = new Parser(new TaskList(), new Ui(),
                new Storage(tempDirectory.resolve("tasks.csv")));
    }

    /**
     * Restores the process-wide input stream after constructing the UI dependency.
     */
    @AfterEach
    void restoreInput() {
        System.setIn(originalInput);
    }

    /**
     * Verifies to-do parsing trims descriptions and rejects blank descriptions.
     */
    @Test
    void parseToDo_validAndBlankDescriptions_returnsTaskOrThrows() throws Exception {
        ToDo task = parser.parseToDo("  read book  ");

        assertEquals("read book", task.getStorageFields().get(0));
        assertThrows(EmptyTaskDescriptionException.class, () -> parser.parseToDo("   "));
    }

    /**
     * Verifies valid deadline parsing and its required-parameter validation.
     */
    @Test
    void parseDeadline_validAndMissingParameter_returnsTaskOrThrows() throws Exception {
        Deadline deadline = parser.parseDeadline(" return book /by 2019-12-02 ");

        assertEquals("return book", deadline.getStorageFields().get(0));
        assertEquals("2019-12-02", deadline.getStorageFields().get(1));
        assertThrows(EmptyTaskDescriptionException.class, () -> parser.parseDeadline("   "));
        assertThrows(EmptyParameterException.class, () -> parser.parseDeadline(" /by 2019-12-02"));
        assertThrows(EmptyParameterException.class, () -> parser.parseDeadline("return book"));
        assertThrows(EmptyParameterException.class, () -> parser.parseDeadline("return book /by"));
        assertThrows(InvalidDateTimeException.class, () -> parser.parseDeadline("return book /by not-a-date"));
    }

    /**
     * Verifies valid event parsing and validation of its two date parameters.
     */
    @Test
    void parseEvent_validAndMissingParameter_returnsTaskOrThrows() throws Exception {
        Event event = parser.parseEvent(
                " project meeting /from 2019-12-02 14:00 /to 2019-12-02 15:00 ");

        assertEquals("project meeting", event.getStorageFields().get(0));
        assertEquals("2019-12-02T14:00:00", event.getStorageFields().get(1));
        assertEquals("2019-12-02T15:00:00", event.getStorageFields().get(2));
        assertThrows(EmptyTaskDescriptionException.class, () -> parser.parseEvent("   "));
        assertThrows(EmptyParameterException.class, () -> parser.parseEvent(
                " /from 2019-12-02 /to 2019-12-03"));
        assertThrows(EmptyParameterException.class, () -> parser.parseEvent("project meeting"));
        assertThrows(EmptyParameterException.class, () -> parser.parseEvent(
                "project meeting /to 2019-12-03"));
        assertThrows(EmptyParameterException.class, () -> parser.parseEvent(
                "project meeting /from /to 2019-12-03"));
        assertThrows(EmptyParameterException.class, () -> parser.parseEvent(
                "project meeting /from 2019-12-02 /to"));
        assertThrows(InvalidDateTimeException.class, () -> parser.parseEvent(
                "project meeting /from invalid /to 2019-12-03"));
    }

    /**
     * Verifies that complete command lines produce the expected command types.
     */
    @Test
    void parse_supportedCommands_returnsMatchingCommandObjects() throws Exception {
        assertInstanceOf(ExitCommand.class, parser.parse("bye"));
        assertInstanceOf(ListCommand.class, parser.parse("list time"));
        assertInstanceOf(MarkCommand.class, parser.parse("mark 1"));
        assertInstanceOf(UnmarkCommand.class, parser.parse("unmark 1"));
        assertInstanceOf(DeleteCommand.class, parser.parse("delete 1"));
        assertInstanceOf(FindCommand.class, parser.parse("find book"));
        assertInstanceOf(HelpCommand.class, parser.parse("help"));
        assertInstanceOf(HelpCommand.class, parser.parse("help list"));
        assertInstanceOf(AddCommand.class, parser.parse("todo read book"));
        assertInstanceOf(AddCommand.class,
                parser.parse("deadline return book /by 2019-12-02"));
        assertInstanceOf(AddCommand.class,
                parser.parse("event meeting /from 2019-12-02 /to 2019-12-03"));
    }

    /**
     * Verifies that unknown commands are reported before command creation.
     */
    @Test
    void parse_unknownCommand_throwsUnknownCommandException() {
        assertThrows(UnknownCommandException.class, () -> parser.parse("unknown"));
        assertThrows(UnknownCommandException.class, () -> parser.parse(null));
    }
}
