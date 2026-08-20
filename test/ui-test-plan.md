# CharlieK UI test plan

This plan contains end-to-end console tests for `CharlieK`.

## Execution information

- Run from the repository root.
- Use Java 25.
- Compile before testing:

  ```
  javac -d _temp/ui-test-classes src/main/java/CharlieK.java src/main/java/Task.java
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
buy milk
list
mark 1
list
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
     added: buy milk
____________________________________________________________
____________________________________________________________
     Here are the tasks in your list:
     1.[ ] buy milk
____________________________________________________________
____________________________________________________________
     Nice! I've marked this task as done:
       [X] buy milk
____________________________________________________________
____________________________________________________________
     Here are the tasks in your list:
     1.[X] buy milk
____________________________________________________________
____________________________________________________________
     OK, I've marked this task as not done yet:
       [ ] buy milk
____________________________________________________________
____________________________________________________________
     Here are the tasks in your list:
     1.[ ] buy milk
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
