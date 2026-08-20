# CharlieK UI test plan

This plan contains end-to-end console tests for `CharlieK`.

## Execution information

- Run from the repository root.
- Use Java 25.
- Compile before testing:

  ```
  javac -d _temp/ui-test-classes src/main/java/CharlieK.java src/main/java/Task.java src/main/java/ToDo.java src/main/java/Deadline.java src/main/java/Event.java
  ```

- Each test case starts a fresh process with:

  ```
  java -cp _temp/ui-test-classes CharlieK
  ```

- Compare output exactly after normalizing only platform line endings. The skill must stop at the first failure and show the complete console transcript.

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
java -cp _temp/ui-test-classes CharlieK
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
java -cp _temp/ui-test-classes CharlieK
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
java -cp _temp/ui-test-classes CharlieK
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
java -cp _temp/ui-test-classes CharlieK
```

**Inputs:**

```
todo borrow book
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
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
       [D][ ] return book (by: Sunday)
     Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
     Got it. I've added this task:
       [E][ ] project meeting (from: Mon 2pm to: 4pm)
     Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] borrow book
     2.[D][ ] return book (by: Sunday)
     3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
     Bye. Hope to see you again soon!
____________________________________________________________
```

### UI-05 — Preserve date and time text literally

**Aim:** Verify that deadline and event date/time values are treated as strings and are not validated or reformatted.

**Command:**

```
java -cp _temp/ui-test-classes CharlieK
```

**Inputs:**

```
deadline do homework /by no idea :-p
event orientation week /from 4/10/2019 /to 11/10/2019
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
       [D][ ] do homework (by: no idea :-p)
     Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
     Got it. I've added this task:
       [E][ ] orientation week (from: 4/10/2019 to: 11/10/2019)
     Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
     Here are the tasks in your list:
     1.[D][ ] do homework (by: no idea :-p)
     2.[E][ ] orientation week (from: 4/10/2019 to: 11/10/2019)
____________________________________________________________
____________________________________________________________
     Bye. Hope to see you again soon!
____________________________________________________________
```
### UI-06 — Reject non-keyword task additions

**Aim:** Verify that entering a command without a task keyword does not add a task.

**Command:**

```
java -cp _temp/ui-test-classes CharlieK
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
     Please use todo, deadline, or event to add a task.
____________________________________________________________
____________________________________________________________
     Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
     Bye. Hope to see you again soon!
____________________________________________________________
```
