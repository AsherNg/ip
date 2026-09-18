# CharlieK User Guide

CharlieK is a friendly task manager for to-dos, deadlines, and events. It saves your task list automatically while you work.

## Quick start

You need JDK 25 or later.

From the project folder, start the GUI with:

```text
./gradlew run
```

On Windows PowerShell, use:

```text
.\gradlew.bat run
```

Type a command in the box and press **Enter** or **Send**. Scroll up to review earlier messages. Enter `bye` when you are done; the input controls are then disabled.

On the first run, CharlieK creates `data/charliek.csv` and adds a small set of sample tasks. Your later changes are saved automatically.

## Command format

- Commands are lowercase, for example `list` rather than `LIST`.
- Replace values in angle brackets with your own text; do not type the brackets.
- Use one ordinary space between words. Extra spaces, tabs, and line breaks are rejected so that commands are not misread.
- Task numbers start at `1` and refer to the order shown by `list`.
- Use `help` if you forget a command. Use `help <command>` for its syntax and an example.

## Features

### Add tasks

Add an undated to-do:

```text
todo buy milk
```

Add a deadline. The `/by` value can be a date, a time, or both:

```text
deadline return book /by 2026-06-07
deadline review report /by 2/12/2019 1800
```

Add an event with both a start and an end:

```text
event project meeting /from 2026-08-06 14:00 /to 2026-08-06 16:00
```

An event must end after it starts. Descriptions may contain punctuation, including commas and quotation marks.

### View and search tasks

```text
list
```

Shows every task in the order it was added.

```text
list time
```

Shows deadlines and events in chronological order, followed by undated to-dos.

```text
find book
```

Shows tasks whose descriptions contain `book`, without regard to letter case.

`find` is for viewing only. Because search results are displayed as a new, shortened list, use `list` before `mark`, `unmark`, or `delete` if you need to act on a search result.

### Complete, undo, or delete tasks

```text
mark 1
unmark 1
delete 1
```

- `mark` marks a task as complete.
- `unmark` changes it back to incomplete.
- `delete` removes it and renumbers the remaining tasks.

All successful changes are saved automatically.

### Get help or exit

```text
help
help deadline
bye
```

`help` lists every command. `help deadline` shows detailed usage for one command. `bye` ends the session.

## Dates and times

CharlieK accepts common date formats, including:

```text
2/12/2019
2019-12-2
2 Dec 2019
```

Times can be written in 24-hour or 12-hour form:

```text
1800
18:00
6pm
6:30 pm
```

You can separate a date and time with a space, a comma, or `T`. Dates are displayed in a readable form, for example:

```text
[D][ ] return book (by: 7 Jun 2026)
[E][ ] project meeting (from: 6 Aug 2026, 14:00 to: 6 Aug 2026, 16:00)
```

## Saved data and common errors

CharlieK stores tasks in `data/charliek.csv`, relative to the folder from which you start the application.

- A missing data file is created with the starter tasks.
- An existing file, including an intentionally empty file, is loaded as-is.
- A malformed row is skipped so that valid tasks can still be loaded.
- If the file cannot be read or saved, CharlieK displays an explanation and keeps the session running where possible.
- If you edit the CSV file yourself, make a backup first and use `list` to verify the result after restarting.

Invalid commands, missing descriptions, bad task numbers, duplicate tasks, unsupported dates, and invalid event ranges are reported with a helpful message. The application does not exit just because one command is wrong; correct the command or enter `help` and try again.

## Example session

```text
todo buy milk
deadline submit report /by 2/12/2019
list time
mark 1
find report
bye
```
