package charliek.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import charliek.model.Task;
import charliek.model.ToDo;

/**
 * Tests UI message rendering for output destinations shared by the console and GUI.
 */
class UiTest {
    /**
     * Verifies that a custom output sink receives the complete formatted response.
     */
    @Test
    void outputSink_taskAddedMessage_receivesFormattedResponse() {
        StringBuilder output = new StringBuilder();
        Ui ui = new Ui(output::append);

        ui.showTaskAdded(new ToDo("read book"), 1);

        String lineSeparator = System.lineSeparator();
        assertEquals("     Got it. I've added this task:" + lineSeparator
                + "       [T][ ] read book" + lineSeparator
                + "     Now you have 1 tasks in the list." + lineSeparator, output.toString());
    }

    /**
     * Verifies that normal and matching task lists share consistent numbering and formatting.
     */
    @Test
    void taskListMessages_differentHeadings_preserveTaskFormatting() {
        List<Task> tasks = List.of(new ToDo("read book"));
        StringBuilder output = new StringBuilder();
        Ui ui = new Ui(output::append);
        String lineSeparator = System.lineSeparator();

        ui.showTasks(tasks);
        ui.showMatchingTasks(tasks);

        assertEquals("     Here are the tasks in your list:" + lineSeparator
                + "     1.[T][ ] read book" + lineSeparator
                + "     Here are the matching tasks in your list:" + lineSeparator
                + "     1.[T][ ] read book" + lineSeparator, output.toString());
    }
}
