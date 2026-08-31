# CharlieK

CharlieK is a command-line task manager written in Java 25. It supports to-dos, deadlines, events, completion tracking, keyword search, chronological listing, and automatic CSV persistence.

## Requirements

- JDK 25
- Windows PowerShell, macOS, or Linux

## Running the application

From the repository root, run:

```text
./gradlew run
```

On Windows PowerShell, use:

```text
.\gradlew.bat run
```

CharlieK stores tasks in `data/charliek.csv`. The directory and file are created automatically when a task is first saved.

## Commands

| Command | Description |
| --- | --- |
| `todo <description>` | Add an undated to-do |
| `deadline <description> /by <date/time>` | Add a deadline |
| `event <description> /from <date/time> /to <date/time>` | Add an event |
| `list` | Display tasks in insertion order |
| `list time` | Display dated tasks chronologically, followed by to-dos |
| `find <keyword>` | Display tasks whose descriptions contain the keyword |
| `mark <number>` | Mark a task as complete |
| `unmark <number>` | Mark a task as incomplete |
| `delete <number>` | Delete a task |
| `bye` | Exit CharlieK |

Task numbers are one-based and refer to the order shown by `list`.

## Date and time examples

CharlieK accepts several numeric and 12-hour formats, including:

```text
deadline submit report /by 2/12/2019
deadline submit report /by 2/12/2019 1800
event project meeting /from 2026-08-06 2pm /to 2026-08-06 4pm
```

Dates are displayed in a readable form such as `2 Dec 2019` and saved internally in ISO format.

## Building and testing

Compile and run the JUnit tests with:

```text
./gradlew test
```

On Windows PowerShell:

```text
.\gradlew.bat test
```

The end-to-end console cases are documented in [`test/ui-test-plan.md`](test/ui-test-plan.md).
