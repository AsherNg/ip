package charliek.storage;

import charliek.exception.TaskStorageException;
import charliek.model.Deadline;
import charliek.model.Event;
import charliek.model.Task;
import charliek.model.ToDo;
import charliek.parser.DateTimeParser;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates reading and writing CharlieK's task data.
 */
public class Storage {
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
     * malformed CSV rows are ignored so one damaged record cannot prevent the
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
                Task task = parseTask(parseCsvLineSafely(line));
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
        } catch (IOException exception) {
            throw new TaskStorageException(
                    "I couldn't load saved tasks. Please check that data/charliek.csv exists and is readable.",
                    exception);
        } catch (RuntimeException exception) {
            throw new TaskStorageException(
                    "I couldn't load saved tasks because the saved data is invalid.", exception);
        }
    }

    /**
     * Saves the supplied task list as CSV rows, creating its parent directory if necessary.
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
        Path temporaryFile = null;
        try {
            for (Task task : tasks) {
                if (task == null) {
                    throw new IllegalArgumentException("The task list cannot contain null tasks.");
                }
                lines.add(toCsvLine(task));
            }

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
        } catch (IOException exception) {
            throw new TaskStorageException(
                    "I couldn't save tasks. Please check that the data folder is writable.",
                    exception);
        } catch (RuntimeException exception) {
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
        } catch (AtomicMoveNotSupportedException | FileAlreadyExistsException | AccessDeniedException exception) {
            Files.move(temporaryFile, taskFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /** Converts one task into type, status, and task-specific CSV columns. */
    private String toCsvLine(Task task) {
        ArrayList<String> fields = new ArrayList<>();
        fields.add(task.getStorageType());
        fields.add(task.isDone() ? "1" : "0");
        fields.addAll(task.getStorageFields());

        StringBuilder line = new StringBuilder();
        for (int i = 0; i < fields.size(); i++) {
            if (i > 0) {
                line.append(',');
            }
            line.append(escapeCsv(fields.get(i)));
        }
        return line.toString();
    }

    /** Parses one CSV row, returning {@code null} for blank or malformed input. */
    private Task parseTask(List<String> fields) {
        if (fields == null || fields.size() < 3) {
            return null;
        }

        String type = fields.get(0);
        String status = fields.get(1);
        String description = fields.get(2);
        if (!("0".equals(status) || "1".equals(status)) || description.isBlank()) {
            return null;
        }

        try {
            Task task;
            switch (type) {
            case "T":
                if (fields.size() != 3) {
                    return null;
                }
                task = new ToDo(description);
                break;
            case "D":
                if (fields.size() != 4 || fields.get(3).isEmpty()) {
                    return null;
                }
                task = new Deadline(description, DateTimeParser.parseStored(fields.get(3)));
                break;
            case "E":
                if (fields.size() != 5 || fields.get(3).isEmpty() || fields.get(4).isEmpty()) {
                    return null;
                }
                task = new Event(description,
                        DateTimeParser.parseStored(fields.get(3)),
                        DateTimeParser.parseStored(fields.get(4)));
                break;
            default:
                return null;
            }
            return restoreStatus(task, status);
        } catch (DateTimeException | IllegalArgumentException exception) {
            // A dated task with a malformed persisted value is not loadable.
            return null;
        }
    }

    /** Restores completion status after constructing a task from its CSV row. */
    private Task restoreStatus(Task task, String status) {
        if ("1".equals(status)) {
            task.markAsDone();
        }
        return task;
    }

    /** Returns a parsed row, or {@code null} when the CSV syntax is invalid. */
    private List<String> parseCsvLineSafely(String line) {
        if (line == null || line.isBlank()) {
            return null;
        }

        try {
            return parseCsvLine(line);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    /** Parses one CSV row, including quoted fields containing commas or quotes. */
    private List<String> parseCsvLine(String line) {
        ArrayList<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean inQuotes = false;
        boolean closedQuote = false;

        for (int i = 0; i < line.length(); i++) {
            char current = line.charAt(i);
            if (inQuotes) {
                if (current == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        field.append('"');
                        i++;
                    } else {
                        inQuotes = false;
                        closedQuote = true;
                    }
                } else {
                    field.append(current);
                }
            } else if (current == ',') {
                fields.add(field.toString());
                field.setLength(0);
                closedQuote = false;
            } else if (current == '"') {
                if (field.length() != 0 || closedQuote) {
                    throw new IllegalArgumentException("Unexpected quote in CSV row.");
                }
                inQuotes = true;
            } else {
                if (closedQuote && !Character.isWhitespace(current)) {
                    throw new IllegalArgumentException("Unexpected content after quoted CSV field.");
                }
                field.append(current);
            }
        }

        if (inQuotes) {
            throw new IllegalArgumentException("Unclosed quoted CSV field.");
        }
        fields.add(field.toString());
        return fields;
    }

    /** Escapes a value when CSV syntax requires quoting. */
    private String escapeCsv(String value) {
        if (value == null) {
            throw new IllegalArgumentException("A task CSV field cannot be null.");
        }
        if (value.indexOf(',') < 0 && value.indexOf('"') < 0
                && value.indexOf('\n') < 0 && value.indexOf('\r') < 0) {
            return value;
        }
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}
