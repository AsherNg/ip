# CharlieK User Guide

CharlieK is a lightweight command-line task manager. It keeps your tasks in memory while you work and saves them automatically to `data/charliek.csv` after each change.

## Adding tasks

Use a task keyword followed by a description.

### To-do

```text
todo buy milk
```

To-dos have no date or time.

### Deadline

```text
deadline return book /by 2026-06-07
```

A deadline may contain a date, a time, or both. For example:

```text
deadline review report /by 2/12/2019 1800
```

### Event

```text
event project meeting /from 2026-08-06 14:00 /to 2026-08-06 16:00
```

Events require both `/from` and `/to` values.

## Viewing and searching tasks

- `list` displays tasks in the order they were added.
- `list time` displays dated tasks by deadline or event start, with undated to-dos last.
- `find <keyword>` displays tasks whose descriptions contain the keyword, ignoring letter case.

For example:

```text
find book
```

Task numbers are one-based. The number shown by `list` can be used with the update and delete commands.

## Updating and deleting tasks

```text
mark 1
unmark 1
delete 1
```

Marking changes completion status. Deleting removes the task and renumbers the remaining list. Changes are saved automatically.

## Date and time formats

CharlieK accepts numeric dates such as `2/12/2019`, ISO-like dates such as `2019-12-2`, and month names such as `2 Dec 2019`. Times can be entered as `1800`, `18:00`, or `6pm`.

Displayed dates use formats such as:

```text
[D][ ] return book (by: 7 Jun 2026)
[E][ ] project meeting (from: 6 Aug 2026, 14:00 to: 6 Aug 2026, 16:00)
```

Invalid commands and malformed parameters are reported without terminating the session. A missing or malformed saved record is ignored so valid records can still be loaded.

## Exiting

Enter:

```text
bye
```

CharlieK prints a goodbye message and ends the session.
