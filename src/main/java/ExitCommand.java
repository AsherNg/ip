/**
 * Handles the command that ends the current application session.
 */
public class ExitCommand extends Command {
    /** The UI used to show the goodbye message. */
    private final Ui ui;

    /**
     * Creates an exit command.
     *
     * @param ui the UI used to show the goodbye message
     */
    public ExitCommand(Ui ui) {
        this.ui = ui;
    }

    @Override
    public void execute() {
        ui.showGoodbye();
    }

    @Override
    public boolean isExit() {
        return true;
    }
}
