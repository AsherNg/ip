package charliek;

import charliek.ui.Main;
import javafx.application.Application;

/**
 * Launches the JavaFX application from a plain Java entry point.
 *
 * <p>Keeping this class separate avoids JavaFX classpath issues when the
 * application is started through Gradle.</p>
 */
public final class Launcher {
    private Launcher() {
    }

    /** Starts the CharlieK JavaFX application. */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
