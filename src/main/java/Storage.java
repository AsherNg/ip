import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Encapsulates reading and writing CharlieK's task data.
 */
public class Storage {
    /** Patterns for the human-readable task lines written to disk. */
    private static final Pattern TODO_LINE = Pattern.compile("^\\[T\\]\\[( |X)\\] (.*)$");
    private static final Pattern DEADLINE_LINE =
            Pattern.compile("^\\[D\\]\\[( |X)\\] (.*) \\(by: (.*)\\)$");
    private static final Pattern EVENT_LINE =
            Pattern.compile("^\\[E\\]\\[( |X)\\] (.*) \\(from: (.*) to: (.*)\\)$");

    /** The normalized absolute path of the task file. */
    private final Path taskFile;

    /**
     * Creates a storage service for a task file.
     *
     * @param taskFile the file used for task persistence
     */
    public Storage(Path taskFile) {
        if (taskFile == null || taskFile.getFileName() == null) {
            throw new IllegalArgumentException("The task file path must name a file.");
        }
        this.taskFile = taskFile.toAbsolutePath().normalize();
    }

    /**
     * Loads all valid tasks from disk.
     *
     * <p>A missing file is treated as a new user's empty task list. Blank and
     * malformed lines are ignored so one damaged record cannot prevent the
     * rest of the task list from loading.</p>
     *
     * @return the tasks found in the file
     * @throws TaskStorageException when the file cannot be inspected or read
     */
    public ArrayList<Task> load() throws TaskStorageException {
        try {
            if (Files.notExists(taskFile)) {
                return new ArrayList<>();
            }
            if (!Files.isRegularFile(taskFile)) {
                throw new TaskStorageException(
                        "I couldn't load saved tasks because the task file path is not a regular file.");
            }
            if (!Files.isReadable(taskFile)) {
                throw new TaskStorageException(
                        "I couldn't load saved tasks because the task file is not readable.");
            }

            ArrayList<Task> tasks = new ArrayList<>();
            for (String line : Files.readAllLines(taskFile, StandardCharsets.UTF_8)) {
                Task task = parseTask(line);
                if (task != null) {
                    tasks.add(task);
                }
            }
            return tasks;
        } catch (TaskStorageException exception) {
            throw exception;
        } catch (NoSuchFileException exception) {
            // A new user may create the file between the existence check and the read.
            return new ArrayList<>();
        } catch (IOException | SecurityException exception) {
            throw new TaskStorageException(
                    "I couldn't load saved tasks. Please check that data/charliek.txt exists and is readable.",
                    exception);
        }
    }

    /**
     * Saves the supplied task list, creating its parent directory if necessary.
     * A temporary file prevents a failed write from truncating a previously
     * valid task file.
     *
     * @param tasks the complete current task list
     * @throws TaskStorageException when the path is not writable or the write fails
     */
    public void save(List<Task> tasks) throws TaskStorageException {
        if (tasks == null) {
            throw new IllegalArgumentException("The task list cannot be null.");
        }

        ArrayList<String> lines = new ArrayList<>();
        for (Task task : tasks) {
            if (task == null) {
                throw new IllegalArgumentException("The task list cannot contain null tasks.");
            }
            lines.add(task.toString());
        }

        Path temporaryFile = null;
        try {
            Path parent = taskFile.getParent();
            Files.createDirectories(parent);
            if (Files.exists(taskFile) && !Files.isRegularFile(taskFile)) {
                throw new IOException("The task path is not a regular file.");
            }

            temporaryFile = Files.createTempFile(parent, "charliek", ".tmp");
            Files.write(temporaryFile, lines, StandardCharsets.UTF_8,
                    StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
            moveIntoPlace(temporaryFile);
            temporaryFile = null;
        } catch (IOException | SecurityException exception) {
            throw new TaskStorageException(
                    "I couldn't save tasks. Please check that the data folder is writable.",
                    exception);
        } finally {
            if (temporaryFile != null) {
                try {
                    Files.deleteIfExists(temporaryFile);
                } catch (IOException | SecurityException ignored) {
                    // The original save error is more useful to the user.
                }
            }
        }
    }

    /** Moves a fully written temporary file into the configured task-file path. */
    private void moveIntoPlace(Path temporaryFile) throws IOException {
        try {
            Files.move(temporaryFile, taskFile,
                    StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException | FileAlreadyExistsException exception) {
            Files.move(temporaryFile, taskFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /** Parses one saved task line, returning {@code null} for invalid input. */
    private Task parseTask(String line) {
        Matcher todoMatcher = TODO_LINE.matcher(line);
        if (todoMatcher.matches()) {
            return restoreStatus(new ToDo(todoMatcher.group(2)), todoMatcher.group(1));
        }

        Matcher deadlineMatcher = DEADLINE_LINE.matcher(line);
        if (deadlineMatcher.matches()) {
            return restoreStatus(new Deadline(
                    deadlineMatcher.group(2), deadlineMatcher.group(3)), deadlineMatcher.group(1));
        }

        Matcher eventMatcher = EVENT_LINE.matcher(line);
        if (eventMatcher.matches()) {
            return restoreStatus(new Event(
                    eventMatcher.group(2), eventMatcher.group(3), eventMatcher.group(4)), eventMatcher.group(1));
        }

        return null;
    }

    /** Restores completion status after constructing a task from its saved line. */
    private Task restoreStatus(Task task, String status) {
        if ("X".equals(status)) {
            task.markAsDone();
        }
        return task;
    }
}
