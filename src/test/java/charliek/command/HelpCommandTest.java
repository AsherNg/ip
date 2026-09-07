package charliek.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import charliek.ui.Ui;

/**
 * Tests general and command-specific help rendering.
 */
class HelpCommandTest {
    /**
     * Verifies that general help includes every command's usage and example.
     */
    @Test
    void execute_withoutCommand_showsAllCommandHelp() {
        StringBuilder output = new StringBuilder();
        new HelpCommand(new Ui(output::append), "").execute();

        String help = output.toString();
        for (CommandType command : CommandType.values()) {
            assertTrue(help.contains(command.getUsage()));
            assertTrue(help.contains("Example: " + command.getExample()));
        }
    }

    /**
     * Verifies that detailed help shows the selected command's usage and example.
     */
    @Test
    void execute_withKnownCommand_showsDetailedHelp() {
        StringBuilder output = new StringBuilder();
        new HelpCommand(new Ui(output::append), " list ").execute();

        assertEquals("     Command: list\n"
                + "     Usage: list [time]\n"
                + "     Description: Display tasks in insertion order, or chronologically when time is specified.\n"
                + "     Example: list time\n", output.toString().replace(System.lineSeparator(), "\n"));
    }

    /**
     * Verifies that an unknown help target returns a useful response.
     */
    @Test
    void execute_withUnknownCommand_showsHelpfulError() {
        StringBuilder output = new StringBuilder();
        new HelpCommand(new Ui(output::append), "unknown").execute();

        assertEquals("     I do not have help for 'unknown'. Try 'help' to see the available commands.\n",
                output.toString().replace(System.lineSeparator(), "\n"));
    }
}
