package charliek.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

/** Tests command keyword recognition and argument extraction. */
class CommandTypeTest {
    /** Verifies that every supported command keyword is recognized. */
    @Test
    void fromInput_supportedKeywords_returnsMatchingCommandType() {
        assertEquals(Optional.of(CommandType.BYE), CommandType.fromInput("bye"));
        assertEquals(Optional.of(CommandType.LIST), CommandType.fromInput("list"));
        assertEquals(Optional.of(CommandType.MARK), CommandType.fromInput("mark 1"));
        assertEquals(Optional.of(CommandType.UNMARK), CommandType.fromInput("unmark 1"));
        assertEquals(Optional.of(CommandType.DELETE), CommandType.fromInput("delete 1"));
        assertEquals(Optional.of(CommandType.TODO), CommandType.fromInput("todo read book"));
        assertEquals(Optional.of(CommandType.DEADLINE),
                CommandType.fromInput("deadline return book /by 2019-12-02"));
        assertEquals(Optional.of(CommandType.EVENT),
                CommandType.fromInput("event meeting /from 2019-12-02 /to 2019-12-03"));
        assertEquals(Optional.of(CommandType.FIND), CommandType.fromInput("find book"));
    }

    /** Verifies that unknown, null, and invalid argument forms are rejected. */
    @Test
    void fromInput_unknownOrInvalidInput_returnsEmptyOptional() {
        assertTrue(CommandType.fromInput(null).isEmpty());
        assertTrue(CommandType.fromInput("").isEmpty());
        assertTrue(CommandType.fromInput("unknown command").isEmpty());
        assertTrue(CommandType.fromInput("bye now").isEmpty());
        assertTrue(CommandType.fromInput("todoist something").isEmpty());
    }

    /** Verifies extraction for exact commands and commands with arguments. */
    @Test
    void argumentFrom_exactAndArgumentInputs_returnsTextAfterKeyword() {
        assertEquals("", CommandType.BYE.argumentFrom("bye"));
        assertEquals("", CommandType.TODO.argumentFrom("todo"));
        assertEquals("", CommandType.TODO.argumentFrom("todo "));
        assertEquals("read book", CommandType.TODO.argumentFrom("todo read book"));
        assertEquals("1", CommandType.MARK.argumentFrom("mark 1"));
    }

    /** Verifies that a null input has no argument. */
    @Test
    void argumentFrom_nullInput_returnsEmptyString() {
        assertEquals("", CommandType.TODO.argumentFrom(null));
    }
}
