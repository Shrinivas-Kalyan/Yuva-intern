# Library Book Inventory Management System - Week 3: Unit Testing and Debugging

A console-based Java application that manages a library's book inventory, extended in Week 3 with a full
JUnit 5 test suite, documented debugging of injected bugs, and light refactoring.

**Student:** Shrinivas K  |  **Internship Task:** Week 3  |  **Language:** Java 17+  |  **Testing:** JUnit 5

## Features

* Add a new book (ISBN, title, author, publication year)
* View all books
* Search a book by ISBN
* Update a book's title, author and publication year
* Delete a book
* Inventory kept in a Java `ArrayList`
* Menu-driven console interface
* Input validation and custom exceptions (`InvalidBookException`, `BookNotFoundException`, `DuplicateBookException`)

### Validation rules

| Field | Rule |
|-------|------|
| ISBN | 10 characters (digits, last may be `X`) or 13 digits; hyphens/spaces are ignored; must be unique |
| Title / Author | not null, not blank; surrounding spaces trimmed |
| Publication year | between 1450 and the current year (inclusive) |

## Technologies

Java 17 (works on 21), Maven 3.6+, JUnit Jupiter 5.10.2, Maven Surefire 3.2.5.

## Project structure

```
Week3_UnitTesting_Debugging/
├── pom.xml
├── README.md
├── .gitignore
├── src/
│   ├── main/java/com/library/inventory/
│   │   ├── Book.java                   # book record with validated fields
│   │   ├── BookValidator.java          # all validation rules in one place
│   │   ├── BookInventory.java          # business logic (CRUD), no console I/O
│   │   ├── InventoryApp.java           # menu-driven console UI
│   │   ├── InvalidBookException.java
│   │   ├── BookNotFoundException.java
│   │   └── DuplicateBookException.java
│   └── test/java/com/library/inventory/
│       ├── BookValidatorTest.java      # 12 tests
│       ├── BookInventoryTest.java      # 49 tests (Create / Read / Update / Delete / Edge cases)
│       └── InventoryAppTest.java       # 17 tests (menu driven by scripted input)
└── docs/
    ├── BUG_REPORT.md                   # 5 injected bugs: cause, detection, fix
    ├── debugging/logs/                 # raw output of every bug run
    └── test-results/                   # recorded run of the corrected code
```

## JUnit setup

`pom.xml` declares the single test dependency and pins the Surefire plugin (needed so Maven runs JUnit 5 tests):

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.10.2</version>
    <scope>test</scope>
</dependency>
```

## How to run the application

```bash
mvn clean compile
java -cp target/classes com.library.inventory.InventoryApp
```

## How to execute the unit tests

```bash
mvn clean test
```

Reports are written to `target/surefire-reports/`. Expected: **78 tests, 0 failures, 0 errors, 0 skipped**.

> **Note on verification:** the environment where this project was prepared could not reach Maven Central, so the
> official `mvn clean test` run was *not* performed there. The 78 test sources were compiled and run with a small
> local JUnit-compatible runner instead (78 passed, 0 failed - see `docs/test-results/`). Run `mvn clean test`
> yourself and use *your* output/screenshot as the official result. No code-coverage tool was run, so no coverage
> percentage is claimed.

## Bugs identified and resolved

Five bugs were injected into a scratch copy (never into this source) to prove the tests detect real defects:

| # | Bug | Failing tests with bug | Fixed |
|---|-----|-----------------------|-------|
| 1 | ISBN search used `startsWith` | 1 | yes |
| 2 | Delete removed the first book | 4 | yes |
| 3 | Update modified the first book | 5 | yes |
| 4 | Duplicate check off by one | 4 | yes |
| 5 | Future publication year accepted | 3 | yes |

Details: [`docs/BUG_REPORT.md`](docs/BUG_REPORT.md).

## Refactoring done

* Validation moved from scattered checks into `BookValidator` (no duplicated rules).
* Business logic (`BookInventory`) separated from console I/O (`InventoryApp`) so it is unit testable.
* `InventoryApp` takes `InputStream`/`PrintStream`, so the menu can be tested with scripted input.
* Updates validate everything before changing anything, so a failed update never half-modifies a book.
* `getAllBooks()` returns a read-only copy to protect encapsulation.

## Learning outcomes

* Writing independent JUnit 5 tests with `@BeforeEach`, `@Nested` and `assertThrows`.
* Testing boundary values and invalid input, not just the happy path.
* Using failing tests to locate a bug's root cause, then re-running the whole suite after a fix.
* Designing code (separating UI from logic) so it can be tested.

## Upload to GitHub

```bash
cd Week3_UnitTesting_Debugging
git init
git add .
git commit -m "Week 3: unit testing and debugging for Library Inventory"
git branch -M main
git remote add origin https://github.com/<your-username>/<your-repo>.git
git push -u origin main
```

Create the empty repository on github.com first (no README/.gitignore), then use its URL above.
