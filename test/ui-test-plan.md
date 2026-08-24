# CharlieK UI test plan

This plan contains end-to-end console tests for `CharlieK`.

## Execution information

- Run from the repository root.
- Use Java 25.
- Compile before testing:

  ```
    javac -d _temp/ui-test-classes (Get-ChildItem -Path src/main/java -Recurse -Filter *.java | ForEach-Object { $_.FullName })
  ```

- Each test case starts a fresh process with:

  ```
  java -cp _temp/ui-test-classes charliek.CharlieK
  ```

- Compare output exactly after normalizing only platform line endings. The skill must stop at the first failure and show the complete console transcript.
- Remove `data/charliek.csv` before each test case to keep cases isolated. For setup-based cases, create the CSV file with the contents specified in that test's setup first.
- The CSV file has no header row. Columns are `type,status,description`, followed by `deadline` for `D` tasks or `from,to` for `E` tasks; status `0` means incomplete and `1` means complete. Dates are stored as `yyyy-MM-dd`, and date-times as ISO local date-times such as `yyyy-MM-ddTHH:mm:ss`.

## Shared startup output

Every test case begins with:

```
____________________________________________________________
  ____ _                _ _      _  __
 / ___| |__   __ _ _ __| (_) ___| |/ /
| |   | '_ \ / _` | '__| | |/ _ \ ' / 
| |___| | | | (_| | |  | | |  __/ . \ 
 \____|_| |_|\__,_|_|  |_|_|\___|_|\_\
Hello! I'm CharlieK.
What can I do for you?
____________________________________________________________
```

## Test cases

### UI-01 — Exit command

**Aim:** Verify that the application starts and exits cleanly when the user enters `bye`.

**Command:**

```
java -cp _temp/ui-test-classes charliek.CharlieK
```

**Inputs:**

```
bye
```

**Expected output:**

```
____________________________________________________________
  ____ _                _ _      _  __
 / ___| |__   __ _ _ __| (_) ___| |/ /
| |   | '_ \ / _` | '__| | |/ _ \ ' / 
| |___| | | | (_| | |  | | |  __/ . \ 
 \____|_| |_|\__,_|_|  |_|_|\___|_|\_\
Hello! I'm CharlieK.
What can I do for you?
____________________________________________________________
____________________________________________________________
     Bye. Hope to see you again soon!
____________________________________________________________
```

### UI-02 — Add, list, mark, and unmark a task

**Aim:** Verify task creation, listing, completion marking, and completion reversal in one session.

**Command:**

```
java -cp _temp/ui-test-classes charliek.CharlieK
```

**Inputs:**

```
todo buy milk
list
mark 1
mark 1
list
unmark 1
unmark 1
list
bye
```

**Expected output:**

```
____________________________________________________________
  ____ _                _ _      _  __
 / ___| |__   __ _ _ __| (_) ___| |/ /
| |   | '_ \ / _` | '__| | |/ _ \ ' / 
| |___| | | | (_| | |  | | |  __/ . \ 
 \____|_| |_|\__,_|_|  |_|_|\___|_|\_\
Hello! I'm CharlieK.
What can I do for you?
____________________________________________________________
____________________________________________________________
     Got it. I've added this task:
       [T][ ] buy milk
     Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] buy milk
____________________________________________________________
____________________________________________________________
     Nice! I've marked this task as done:
       [T][X] buy milk
____________________________________________________________
____________________________________________________________
     This task is already marked:
       [T][X] buy milk
____________________________________________________________
____________________________________________________________
     Here are the tasks in your list:
     1.[T][X] buy milk
____________________________________________________________
____________________________________________________________
     OK, I've marked this task as not done yet:
       [T][ ] buy milk
____________________________________________________________
____________________________________________________________
     This task is already unmarked:
       [T][ ] buy milk
____________________________________________________________
____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] buy milk
____________________________________________________________
____________________________________________________________
     Bye. Hope to see you again soon!
____________________________________________________________
```

### UI-03 — Invalid task references

**Aim:** Verify that invalid and out-of-range task references produce helpful errors and do not terminate the application.

**Command:**

```
java -cp _temp/ui-test-classes charliek.CharlieK
```

**Inputs:**

```
mark 1
mark x
unmark 0
bye
```

**Expected output:**

```
____________________________________________________________
  ____ _                _ _      _  __
 / ___| |__   __ _ _ __| (_) ___| |/ /
| |   | '_ \ / _` | '__| | |/ _ \ ' / 
| |___| | | | (_| | |  | | |  __/ . \ 
 \____|_| |_|\__,_|_|  |_|_|\___|_|\_\
Hello! I'm CharlieK.
What can I do for you?
____________________________________________________________
____________________________________________________________
     That task does not exist.
____________________________________________________________
____________________________________________________________
     Please provide a valid task number.
____________________________________________________________
____________________________________________________________
     That task does not exist.
____________________________________________________________
____________________________________________________________
     Bye. Hope to see you again soon!
____________________________________________________________
```

### UI-04 — Add to-do, deadline, and event tasks

**Aim:** Verify that the three supported task types are parsed, stored polymorphically in the task list, and displayed with their type-specific details.

**Command:**

```
java -cp _temp/ui-test-classes charliek.CharlieK
```

**Inputs:**

```
todo borrow book
deadline return book /by 2026-06-07
event project meeting /from 2026-08-06 14:00 /to 2026-08-06 16:00
list
bye
```

**Expected output:**

```
____________________________________________________________
  ____ _                _ _      _  __
 / ___| |__   __ _ _ __| (_) ___| |/ /
| |   | '_ \ / _` | '__| | |/ _ \ ' / 
| |___| | | | (_| | |  | | |  __/ . \ 
 \____|_| |_|\__,_|_|  |_|_|\___|_|\_\
Hello! I'm CharlieK.
What can I do for you?
____________________________________________________________
____________________________________________________________
     Got it. I've added this task:
       [T][ ] borrow book
     Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
     Got it. I've added this task:
       [D][ ] return book (by: 7 Jun 2026)
     Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
     Got it. I've added this task:
       [E][ ] project meeting (from: 6 Aug 2026, 14:00 to: 6 Aug 2026, 16:00)
     Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] borrow book
     2.[D][ ] return book (by: 7 Jun 2026)
     3.[E][ ] project meeting (from: 6 Aug 2026, 14:00 to: 6 Aug 2026, 16:00)
____________________________________________________________
____________________________________________________________
     Bye. Hope to see you again soon!
____________________________________________________________
```

### UI-05 — Parse and normalize date/time variations

**Aim:** Verify that common numeric, ISO, compact-time, and 12-hour date/time inputs are parsed and printed consistently.

**Command:**

```
java -cp _temp/ui-test-classes charliek.CharlieK
```

**Inputs:**

```
deadline review report /by 2/12/2019 1800
event orientation week /from 2/12/2019 6pm /to 2019-12-2 23:00
deadline holiday /by 2-12-2019
list
bye
```

**Expected output:**

```
____________________________________________________________
  ____ _                _ _      _  __
 / ___| |__   __ _ _ __| (_) ___| |/ /
| |   | '_ \ / _` | '__| | |/ _ \ ' / 
| |___| | | | (_| | |  | | |  __/ . \ 
 \____|_| |_|\__,_|_|  |_|_|\___|_|\_\
Hello! I'm CharlieK.
What can I do for you?
____________________________________________________________
____________________________________________________________
     Got it. I've added this task:
       [D][ ] review report (by: 2 Dec 2019, 18:00)
     Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
     Got it. I've added this task:
       [E][ ] orientation week (from: 2 Dec 2019, 18:00 to: 2 Dec 2019, 23:00)
     Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
     Got it. I've added this task:
       [D][ ] holiday (by: 2 Dec 2019)
     Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
     Here are the tasks in your list:
     1.[D][ ] review report (by: 2 Dec 2019, 18:00)
     2.[E][ ] orientation week (from: 2 Dec 2019, 18:00 to: 2 Dec 2019, 23:00)
     3.[D][ ] holiday (by: 2 Dec 2019)
____________________________________________________________
____________________________________________________________
     Bye. Hope to see you again soon!
____________________________________________________________
```
### UI-06 — Reject non-keyword task additions

**Aim:** Verify that entering a command without a task keyword does not add a task.

**Command:**

```
java -cp _temp/ui-test-classes charliek.CharlieK
```

**Inputs:**

```
buy milk
list
bye
```

**Expected output:**

```
____________________________________________________________
  ____ _                _ _      _  __
 / ___| |__   __ _ _ __| (_) ___| |/ /
| |   | '_ \ / _` | '__| | |/ _ \ ' / 
| |___| | | | (_| | |  | | |  __/ . \ 
 \____|_| |_|\__,_|_|  |_|_|\___|_|\_\
Hello! I'm CharlieK.
What can I do for you?
____________________________________________________________
____________________________________________________________
     I do not know what that command means, but I know how to carry the flame!
____________________________________________________________
____________________________________________________________
     Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
     Bye. Hope to see you again soon!
____________________________________________________________
```

### UI-07 — Reject empty descriptions and parameters

**Aim:** Verify that empty task descriptions, missing deadline parameters, and missing event parameters are caught and reported without adding malformed tasks.

**Command:**

```
java -cp _temp/ui-test-classes charliek.CharlieK
```

**Inputs:**

```
todo
deadline
event
deadline return book
deadline return book /by
event project meeting
event project meeting /from Mon 2pm /to
list
bye
```

**Expected output:**

```
____________________________________________________________
  ____ _                _ _      _  __
 / ___| |__   __ _ _ __| (_) ___| |/ /
| |   | '_ \ / _` | '__| | |/ _ \ ' / 
| |___| | | | (_| | |  | | |  __/ . \ 
 \____|_| |_|\__,_|_|  |_|_|\___|_|\_\
Hello! I'm CharlieK.
What can I do for you?
____________________________________________________________
____________________________________________________________
     The description is empty! Enter the description or I will carry the flame!
____________________________________________________________
____________________________________________________________
     The description is empty! Enter the description or I will carry the flame!
____________________________________________________________
____________________________________________________________
     The description is empty! Enter the description or I will carry the flame!
____________________________________________________________
____________________________________________________________
     The parameter is empty! Enter the required parameters or I will carry the flame!
____________________________________________________________
____________________________________________________________
     The parameter is empty! Enter the required parameters or I will carry the flame!
____________________________________________________________
____________________________________________________________
     The parameter is empty! Enter the required parameters or I will carry the flame!
____________________________________________________________
____________________________________________________________
     The parameter is empty! Enter the required parameters or I will carry the flame!
____________________________________________________________
____________________________________________________________
     Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
     Bye. Hope to see you again soon!
____________________________________________________________
```

### UI-08 — Delete a task and compact the list

**Aim:** Verify that a selected task is removed, the remaining tasks are renumbered, and the updated task count is displayed.

**Command:**

```
java -cp _temp/ui-test-classes charliek.CharlieK
```

**Inputs:**

```
todo read book
mark 1
deadline return book /by 2026-06-06
mark 2
event project meeting /from 2026-08-06 14:00 /to 2026-08-06 16:00
todo join sports club
mark 4
todo borrow book
list
delete 3
list
bye
```

**Expected output:**

```
____________________________________________________________
  ____ _                _ _      _  __
 / ___| |__   __ _ _ __| (_) ___| |/ /
| |   | '_ \ / _` | '__| | |/ _ \ ' / 
| |___| | | | (_| | |  | | |  __/ . \ 
 \____|_| |_|\__,_|_|  |_|_|\___|_|\_\
Hello! I'm CharlieK.
What can I do for you?
____________________________________________________________
____________________________________________________________
     Got it. I've added this task:
       [T][ ] read book
     Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
     Nice! I've marked this task as done:
       [T][X] read book
____________________________________________________________
____________________________________________________________
     Got it. I've added this task:
       [D][ ] return book (by: 6 Jun 2026)
     Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
     Nice! I've marked this task as done:
       [D][X] return book (by: 6 Jun 2026)
____________________________________________________________
____________________________________________________________
     Got it. I've added this task:
       [E][ ] project meeting (from: 6 Aug 2026, 14:00 to: 6 Aug 2026, 16:00)
     Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
     Got it. I've added this task:
       [T][ ] join sports club
     Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
     Nice! I've marked this task as done:
       [T][X] join sports club
____________________________________________________________
____________________________________________________________
     Got it. I've added this task:
       [T][ ] borrow book
     Now you have 5 tasks in the list.
____________________________________________________________
____________________________________________________________
     Here are the tasks in your list:
     1.[T][X] read book
     2.[D][X] return book (by: 6 Jun 2026)
     3.[E][ ] project meeting (from: 6 Aug 2026, 14:00 to: 6 Aug 2026, 16:00)
     4.[T][X] join sports club
     5.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
     Noted. I've removed this task:
       [E][ ] project meeting (from: 6 Aug 2026, 14:00 to: 6 Aug 2026, 16:00)
     Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
     Here are the tasks in your list:
     1.[T][X] read book
     2.[D][X] return book (by: 6 Jun 2026)
     3.[T][X] join sports club
     4.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
     Bye. Hope to see you again soon!
____________________________________________________________
```

### UI-09 — Save the task list after changes

**Aim:** Exercise the task-list mutation commands that trigger automatic saving to `data/charliek.csv`.

**Command:**

```
java -cp _temp/ui-test-classes charliek.CharlieK
```

**Inputs:**

```
todo save me
deadline keep me /by 2026-06-06
mark 1
unmark 1
delete 2
list
bye
```

**Expected output:**

```
____________________________________________________________
  ____ _                _ _      _  __
 / ___| |__   __ _ _ __| (_) ___| |/ /
| |   | '_ \ / _` | '__| | |/ _ \ ' / 
| |___| | | | (_| | |  | | |  __/ . \ 
 \____|_| |_|\__,_|_|  |_|_|\___|_|\_\
Hello! I'm CharlieK.
What can I do for you?
____________________________________________________________
____________________________________________________________
     Got it. I've added this task:
       [T][ ] save me
     Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
     Got it. I've added this task:
       [D][ ] keep me (by: 6 Jun 2026)
     Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
     Nice! I've marked this task as done:
       [T][X] save me
____________________________________________________________
____________________________________________________________
     OK, I've marked this task as not done yet:
       [T][ ] save me
____________________________________________________________
____________________________________________________________
     Noted. I've removed this task:
       [D][ ] keep me (by: 6 Jun 2026)
     Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] save me
____________________________________________________________
____________________________________________________________
     Bye. Hope to see you again soon!
____________________________________________________________
```

### UI-10 — Load the saved task list at startup

**Aim:** Verify that to-do, deadline, and event tasks, including their completion status, are reconstructed from the saved file when the chatbot starts.

**Setup:** Before starting the application, create `data/charliek.csv` with exactly:

```
T,1,persisted to-do
D,0,persisted deadline,2019-12-02
E,0,persisted event,2019-12-02T14:00,2019-12-02T15:00
```

**Command:**

```
java -cp _temp/ui-test-classes charliek.CharlieK
```

**Inputs:**

```
list
bye
```

**Expected output:**

```
____________________________________________________________
  ____ _                _ _      _  __
 / ___| |__   __ _ _ __| (_) ___| |/ /
| |   | '_ \ / _` | '__| | |/ _ \ ' / 
| |___| | | | (_| | |  | | |  __/ . \ 
 \____|_| |_|\__,_|_|  |_|_|\___|_|\_\
Hello! I'm CharlieK.
What can I do for you?
____________________________________________________________
____________________________________________________________
     Here are the tasks in your list:
     1.[T][X] persisted to-do
     2.[D][ ] persisted deadline (by: 2 Dec 2019)
     3.[E][ ] persisted event (from: 2 Dec 2019, 14:00 to: 2 Dec 2019, 15:00)
____________________________________________________________
____________________________________________________________
     Bye. Hope to see you again soon!
____________________________________________________________
```

### UI-11 — Create storage for a new user

**Aim:** Verify that a user with no existing `data/` directory can add and list a task without a file-system error.

**Setup:** Before starting the application, ensure that `data/charliek.csv` does not exist. The application must create the missing parent directory and CSV file when the task is added.

**Command:**

```
java -cp _temp/ui-test-classes charliek.CharlieK
```

**Inputs:**

```
todo create data path
list
bye
```

**Expected output:**

```
____________________________________________________________
  ____ _                _ _      _  __
 / ___| |__   __ _ _ __| (_) ___| |/ /
| |   | '_ \ / _` | '__| | |/ _ \ ' / 
| |___| | | | (_| | |  | | |  __/ . \ 
 \____|_| |_|\__,_|_|  |_|_|\___|_|\_\
Hello! I'm CharlieK.
What can I do for you?
____________________________________________________________
____________________________________________________________
     Got it. I've added this task:
       [T][ ] create data path
     Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] create data path
____________________________________________________________
____________________________________________________________
     Bye. Hope to see you again soon!
____________________________________________________________
```

### UI-12 — Ignore malformed saved records

**Aim:** Verify that malformed lines in the task file do not crash startup or prevent valid records from loading.

**Setup:** Before starting the application, create `data/charliek.csv` with exactly:

```
not a CSV record
T,1,valid saved task
D,0,valid saved deadline,2019-12-02
```

**Command:**

```
java -cp _temp/ui-test-classes charliek.CharlieK
```

**Inputs:**

```
list
bye
```

**Expected output:**

```
____________________________________________________________
  ____ _                _ _      _  __
 / ___| |__   __ _ _ __| (_) ___| |/ /
| |   | '_ \ / _` | '__| | |/ _ \ ' / 
| |___| | | | (_| | |  | | |  __/ . \ 
 \____|_| |_|\__,_|_|  |_|_|\___|_|\_\
Hello! I'm CharlieK.
What can I do for you?
____________________________________________________________
____________________________________________________________
     Here are the tasks in your list:
     1.[T][X] valid saved task
     2.[D][ ] valid saved deadline (by: 2 Dec 2019)
____________________________________________________________
____________________________________________________________
     Bye. Hope to see you again soon!
____________________________________________________________
```

### UI-13 — Report an unusable task path and roll back changes

**Aim:** Verify that an unusable task path produces a helpful error and that a failed save does not leave an unsaved task in memory.

**Setup:** Before starting the application, create a directory named `data/charliek.csv` instead of a regular file.

**Command:**

```
java -cp _temp/ui-test-classes charliek.CharlieK
```

**Inputs:**

```
todo should not save
list
bye
```

**Expected output:**

```
____________________________________________________________
  ____ _                _ _      _  __
 / ___| |__   __ _ _ __| (_) ___| |/ /
| |   | '_ \ / _` | '__| | |/ _ \ ' / 
| |___| | | | (_| | |  | | |  __/ . \ 
 \____|_| |_|\__,_|_|  |_|_|\___|_|\_\
Hello! I'm CharlieK.
What can I do for you?
____________________________________________________________
     I couldn't load saved tasks because the task file path is not a regular file.
____________________________________________________________
____________________________________________________________
     I couldn't save tasks. Please check that the data folder is writable.
____________________________________________________________
____________________________________________________________
     Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
     Bye. Hope to see you again soon!
____________________________________________________________
```

### UI-14 — Preserve commas and quotes in CSV fields

**Aim:** Verify that commas and quotes inside descriptions and timing values are escaped when saved and reconstructed correctly when loaded.

**Setup:** Before starting the application, create `data/charliek.csv` with exactly:

```
T,0,"buy, milk"
D,1,"return, book","2019-06-06"
E,0,"project ""sync""","2019-08-06T14:00","2019-08-06T16:00"
```

**Command:**

```
java -cp _temp/ui-test-classes charliek.CharlieK
```

**Inputs:**

```
list
bye
```

**Expected output:**

```
____________________________________________________________
  ____ _                _ _      _  __
 / ___| |__   __ _ _ __| (_) ___| |/ /
| |   | '_ \ / _` | '__| | |/ _ \ ' / 
| |___| | | | (_| | |  | | |  __/ . \ 
 \____|_| |_|\__,_|_|  |_|_|\___|_|\_\
Hello! I'm CharlieK.
What can I do for you?
____________________________________________________________
____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] buy, milk
     2.[D][X] return, book (by: 6 Jun 2019)
     3.[E][ ] project "sync" (from: 6 Aug 2019, 14:00 to: 6 Aug 2019, 16:00)
____________________________________________________________
____________________________________________________________
     Bye. Hope to see you again soon!
____________________________________________________________
```

### UI-15 — Reject unsupported date/time formats

**Aim:** Verify that malformed deadline and event date/time values are reported without terminating the application or adding tasks.

**Command:**

```
java -cp _temp/ui-test-classes charliek.CharlieK
```

**Inputs:**

```
deadline submit report /by not a date
event planning /from 2/12/2019 /to invalid
list
bye
```

**Expected output:**

```
____________________________________________________________
  ____ _                _ _      _  __
 / ___| |__   __ _ _ __| (_) ___| |/ /
| |   | '_ \ / _` | '__| | |/ _ \ ' / 
| |___| | | | (_| | |  | | |  __/ . \ 
 \____|_| |_|\__,_|_|  |_|_|\___|_|\_\
Hello! I'm CharlieK.
What can I do for you?
____________________________________________________________
____________________________________________________________
     I couldn't understand that date/time! Try 2/12/2019 or 2/12/2019 6pm.
____________________________________________________________
____________________________________________________________
     I couldn't understand that date/time! Try 2/12/2019 or 2/12/2019 6pm.
____________________________________________________________
____________________________________________________________
     Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
     Bye. Hope to see you again soon!
____________________________________________________________
```

### UI-16 — Sort tasks chronologically with `list time`

**Aim:** Verify that `list time` sorts dated tasks by their deadline or event start, places undated to-dos last, and leaves ordinary `list` order unchanged.

**Command:**

```
java -cp _temp/ui-test-classes charliek.CharlieK
```

**Inputs:**

```
todo no date
deadline later /by 2026-12-31
event early /from 2026-01-01 /to 2026-01-02
deadline middle /by 2026-06-01 1200
list time
list
bye
```

**Expected output:**

```
____________________________________________________________
  ____ _                _ _      _  __
 / ___| |__   __ _ _ __| (_) ___| |/ /
| |   | '_ \ / _` | '__| | |/ _ \ ' / 
| |___| | | | (_| | |  | | |  __/ . \ 
 \____|_| |_|\__,_|_|  |_|_|\___|_|\_\
Hello! I'm CharlieK.
What can I do for you?
____________________________________________________________
____________________________________________________________
     Got it. I've added this task:
       [T][ ] no date
     Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
     Got it. I've added this task:
       [D][ ] later (by: 31 Dec 2026)
     Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
     Got it. I've added this task:
       [E][ ] early (from: 1 Jan 2026 to: 2 Jan 2026)
     Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
     Got it. I've added this task:
       [D][ ] middle (by: 1 Jun 2026, 12:00)
     Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
     Here are the tasks in your list:
     1.[E][ ] early (from: 1 Jan 2026 to: 2 Jan 2026)
     2.[D][ ] middle (by: 1 Jun 2026, 12:00)
     3.[D][ ] later (by: 31 Dec 2026)
     4.[T][ ] no date
____________________________________________________________
____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] no date
     2.[D][ ] later (by: 31 Dec 2026)
     3.[E][ ] early (from: 1 Jan 2026 to: 2 Jan 2026)
     4.[D][ ] middle (by: 1 Jun 2026, 12:00)
____________________________________________________________
____________________________________________________________
     Bye. Hope to see you again soon!
____________________________________________________________
```

### UI-17 — Handle an unsupported `list` option

**Aim:** Verify that an unsupported argument to `list` is reported without terminating the application.

**Command:**

```
java -cp _temp/ui-test-classes charliek.CharlieK
```

**Inputs:**

```
list unsupported
bye
```

**Expected output:**

```
____________________________________________________________
  ____ _                _ _      _  __
 / ___| |__   __ _ _ __| (_) ___| |/ /
| |   | '_ \ / _` | '__| | |/ _ \ ' / 
| |___| | | | (_| | |  | | |  __/ . \ 
 \____|_| |_|\__,_|_|  |_|_|\___|_|\_\
Hello! I'm CharlieK.
What can I do for you?
____________________________________________________________
____________________________________________________________
     I do not know what that command means, but I know how to carry the flame!
____________________________________________________________
____________________________________________________________
     Bye. Hope to see you again soon!
____________________________________________________________
```
