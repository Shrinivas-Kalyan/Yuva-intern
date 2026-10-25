# Library Book Inventory Management System - Week 4: Code Refactoring and Optimization

A console-based Java application that manages a library's book inventory (add, view, search by ISBN, update,
delete). This Week 4 version refactors the Week 3 code for readability, maintainability and efficiency
**without changing any application behaviour**.

## Student information

| | |
|---|---|
| **Student** | Shrinivas K |
| **Email** | shrinivask006@gmail.com |
| **GitHub profile** | https://github.com/Shrinivas-Kalyan |
| **Internship task** | Week 4 - Code Refactoring and Optimization |
| **Language / tools** | Java 17+, JUnit 5, Maven |

## Project objectives

Review the existing code, remove duplication (DRY), separate responsibilities, apply relevant SOLID principles,
choose a better data structure, improve exception handling, document the changes and prove with tests that
nothing broke.

## Features

* Add, view, search (by ISBN), update and delete books
* ISBN accepted as 10 or 13 digits (hyphens/spaces ignored, last character of a 10-digit ISBN may be `X`), unique per book
* Title and author must not be blank; publication year between 1450 and the current year
* Friendly error messages instead of crashes for invalid input, duplicate ISBN, missing book, bad menu choice and end of input

## Technologies used

Java 17 (tested on 21), Maven, JUnit Jupiter 5.10.2 (declared in `pom.xml`), Maven Surefire 3.2.5.

## Project structure

```
LibraryInventorySystem/
├── pom.xml
├── README.md
├── benchmark/
│   └── InventoryBenchmark.java            # before/after timing program (not part of the Maven build)
├── docs/
│   ├── Week4_Code_Refactoring_Optimization_Report.docx
│   ├── benchmark/                         # raw benchmark output, Week 3 code vs Week 4 code
│   └── test-results/                      # raw test run output, Week 3 baseline vs Week 4
└── src/
    ├── main/java/com/library/inventory/
    │   ├── InventoryApp.java              # entry point + table-driven console menu
    │   ├── InputHandler.java              # NEW: reads and converts console input
    │   ├── InventoryService.java          # NEW: interface the menu depends on
    │   ├── BookInventory.java             # inventory logic, now LinkedHashMap based
    │   ├── Book.java                      # book model (new atomic updateDetails)
    │   ├── BookValidator.java             # all validation rules (shared requireText)
    │   ├── InventoryException.java        # NEW: common parent of the three exceptions
    │   ├── InvalidBookException.java
    │   ├── DuplicateBookException.java
    │   └── BookNotFoundException.java
    └── test/java/com/library/inventory/
        ├── BookInventoryTest.java         # Week 3 (unchanged)
        ├── BookValidatorTest.java         # Week 3 (unchanged)
        ├── InventoryAppTest.java          # Week 3 (unchanged)
        ├── InputHandlerTest.java          # NEW (5 tests)
        └── RefactoringBehaviourTest.java  # NEW (12 tests)
```

## Existing code analysis (Week 3 code)

The Week 3 code was already well organised (validator class, custom exceptions, injectable streams, 78 tests).
The real issues found in it:

| File | Existing problem | Improvement | Benefit |
|------|------------------|-------------|---------|
| `BookInventory` | `ArrayList` + linear `indexOf` scan for add (duplicate check), find, update, delete | `LinkedHashMap<String, Book>` keyed by normalized ISBN | Average O(1) instead of O(n); order preserved |
| `BookInventory` | Update did 2 lookups and validated every value twice (`updateBook` + setters) | One lookup + `Book.updateDetails` validates once, atomically | Less work, no half-updated book |
| `BookValidator` | Title, author and ISBN repeated the same null/blank check | One private `requireText(value, fieldName)` | One rule, one message format |
| `BookValidator` | `String.matches` compiled a regex on every call | Two precompiled `Pattern` constants | No repeated pattern compilation |
| `InventoryApp` | 4 menu actions each had their own `try/catch` printing `"Error: "` | One `runSafely(...)`, catching the new `InventoryException` base | 6 catch blocks reduced to 2 in this class |
| `InventoryApp` | Menu text, `switch` and "1 to 6" hint described the same menu in 3 places | One menu table drives printing, dispatch and hint | New option = one line (Open/Closed) |
| `InventoryApp` | Add and update repeated the title/author/year prompting | Shared `readBookDetails(Prompts)` | Prompting written once |
| `InventoryApp` | Mixed menu logic with input reading and parsing | New `InputHandler` class | Single Responsibility |
| `InventoryApp` | Depended directly on `BookInventory` | Depends on `InventoryService` interface | Dependency Inversion |

## Refactoring techniques

* **DRY:** `requireText`, `runSafely`, `readBookDetails`, `Book.updateDetails`.
* **Modularization:** `InputHandler` (input), `InventoryService` (contract), `InventoryException` (error type); `Book`, `BookValidator`, `BookInventory`, `InventoryApp` keep their original responsibilities.
* **SOLID:** *S* - input reading moved out of the menu class; *O* - menu table; *D* - `InventoryApp` -> `InventoryService`.
* **Data structure:** `ArrayList<Book>` -> `LinkedHashMap<String, Book>`.
* **Exception handling:** one `InventoryException` base; `NoSuchElementException` (end of input) is still handled in `run()` and never swallowed.

## Data structure comparison

| Operation | `ArrayList<Book>` (Week 3) | `LinkedHashMap<String, Book>` (Week 4) |
|-----------|---------------------------|----------------------------------------|
| Duplicate ISBN check | O(n) scan | average O(1) (`putIfAbsent`) |
| Search by ISBN | O(n) scan | average O(1) |
| Add | O(n) (duplicate scan) + O(1) append | average O(1) |
| Update (by ISBN) | O(n) to find book | average O(1) to find book |
| Delete (by ISBN) | O(n) find + O(n) shift | average O(1) |
| View all | O(n) | O(n) |

`LinkedHashMap` was chosen over `HashMap` because the menu lists books in insertion order and the Week 3 tests expect it.

## Performance (measured, not estimated)

`benchmark/InventoryBenchmark.java` was run against the Week 3 classes and the Week 4 classes: same program, same
machine, JDK 21.0.12, median of 7 runs after warm-up. Total milliseconds for N operations. Raw output is in `docs/benchmark/`.

| N books | Add (before / after) | Search (before / after) | Delete (before / after) |
|---------|----------------------|-------------------------|-------------------------|
| 1,000   | 7.52 / 8.55          | 2.89 / 5.95             | 7.00 / 1.76             |
| 5,000   | 141.25 / 23.76       | 78.17 / 10.31           | 49.83 / 8.00            |
| 20,000  | 1073.34 / 50.23      | 798.91 / 22.19          | 700.58 / 18.49          |

**How to read this:** at N = 1,000 there is no reliable difference (add and search were not faster, and differences of a few
milliseconds are within normal run-to-run noise on a shared machine). The benefit appears as N grows, because the old code's
work grows with N per operation while the new code's does not. These figures come from one machine and are not a guarantee for others.

## Before-and-after example

```java
// BEFORE - BookInventory (linear scan on every operation)
private int indexOf(String normalizedIsbn) {
    for (int i = 0; i < books.size(); i++) {
        if (books.get(i).getIsbn().equals(normalizedIsbn)) { return i; }
    }
    return -1;
}

// AFTER - lookup, duplicate check and removal are map operations
if (booksByIsbn.putIfAbsent(book.getIsbn(), book) != null) {
    throw new DuplicateBookException(book.getIsbn());
}
```

## Testing

| Run | Tests found | Passed | Failed | Skipped |
|-----|-------------|--------|--------|---------|
| Week 3 code, unmodified (baseline) | 78 | 78 | 0 | 0 |
| Week 4 code, all 78 Week 3 tests unchanged + 17 new | 95 | 95 | 0 | 0 |

**How these were run:** the environment used to prepare this project could not download from Maven Central, so
`mvn clean test` itself could **not** be executed there. The tests were compiled with `javac` and run with the
JUnit 5 console launcher instead (raw output in `docs/test-results/`). Please run `mvn clean test` on your own machine to confirm.

## How to run the application

```bash
mvn clean package
java -cp target/classes com.library.inventory.InventoryApp
```

Without Maven (JDK 17+ only):

```bash
mkdir -p out && javac -d out src/main/java/com/library/inventory/*.java
java -cp out com.library.inventory.InventoryApp
```

## How to execute the unit tests

```bash
mvn clean test
```

## Learning outcomes

* Spotting repetition and removing it with small, well-named methods.
* Choosing a data structure from the operations that matter, and explaining it with Big O.
* Refactoring safely: keep the existing tests green after every change.
* Reporting performance honestly: measure it, and say where there is no improvement.

## Conclusion

The Week 3 code already worked and was well tested, so the refactoring was targeted rather than a rewrite: one data-structure
change, three removed repetitions, one class extracted, one interface introduced. All 78 original tests pass unchanged and
17 new tests were added.

---
Author: Shrinivas K - shrinivask006@gmail.com - https://github.com/Shrinivas-Kalyan
