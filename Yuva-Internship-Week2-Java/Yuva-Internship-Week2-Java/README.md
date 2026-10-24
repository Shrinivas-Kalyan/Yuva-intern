# Library Book Inventory Management System (Java)

**Yuva Internship – Week 2 Task: Java Application Development**

A beginner-friendly, menu-driven Java command-line application that manages a library's book inventory using CRUD operations (Create, Read, Update, Delete). Books are stored in memory using an `ArrayList`; no database is used.

## Objectives

- Build a complete Java CLI application using core Java only.
- Apply object-oriented concepts: classes, objects, encapsulation.
- Implement CRUD operations on an `ArrayList`.
- Validate user input and handle errors so the program does not crash.
- Practise clean, commented, well-structured code.

## Features

- Add a book (title, author, ISBN, publication year) with validation
- Duplicate ISBN prevention (hyphens and spaces are ignored, so `978-0-13-468599-1` equals `9780134685991`)
- View all books in a readable format, with a message when the inventory is empty
- Update title, author and publication year by ISBN (press Enter to keep a current title or author)
- Delete a book by ISBN, with a yes/no confirmation
- Search a book by ISBN
- Menu loop that runs until the user chooses Exit
- Input handling for empty text, non-numeric input, invalid menu choices, invalid ISBN, invalid year and books not found

## Technologies Used

| Item | Details |
|---|---|
| Language | Java (JDK 8 or later; developed against JDK 21) |
| Collections | `java.util.ArrayList` |
| Input | `java.util.Scanner` |
| Interface | Command-line (CLI) |
| Storage | In-memory only (data is lost when the program exits) |

## Project Structure

```
Yuva-Internship-Week2-Java/
├── src/
│   ├── Book.java                     # Book model: fields, validation, display
│   ├── Library.java                  # Inventory logic: add, find, update, delete
│   └── LibraryManagementSystem.java  # main(): menu and user input
├── README.md
├── TESTING_CHECKLIST.md
├── GITHUB_SUBMISSION.md
├── .gitignore
└── Week2_Java_Library_Management_Report.docx
```

## Java Installation Requirements

You need the **JDK** (not just the JRE), because `javac` is required to compile.

1. Install JDK 8 or newer (for example from https://adoptium.net).
2. Check the installation:
   ```
   java -version
   javac -version
   ```
   Both commands must print a version number.

## How to Compile

Run from the project root folder (the folder that contains `src`):

```
javac -d out src/*.java
```

> On Windows PowerShell, if `src/*.java` is not expanded, use: `javac -d out src\Book.java src\Library.java src\LibraryManagementSystem.java`

## How to Run

```
java -cp out LibraryManagementSystem
```

## Example Menu Output

```
===== LIBRARY BOOK INVENTORY MANAGEMENT SYSTEM =====

1. Add Book
2. View All Books
3. Update Book
4. Delete Book
5. Search Book by ISBN
6. Exit

Enter your choice: 1

--- Add New Book ---
Enter book title: Effective Java
Enter author name: Joshua Bloch
Enter ISBN (10 or 13 digits): 9780134685991
Enter publication year: 2018
Book added successfully! Assigned Book ID: 1
```

Viewing books:

```
--- All Books ---
--------------------------------------------
Book ID          : 1
Title            : Effective Java
Author           : Joshua Bloch
ISBN             : 9780134685991
Publication Year : 2018
--------------------------------------------
Total books: 1
```

## CRUD Operations Explained

| Operation | Menu option | What happens |
|---|---|---|
| **Create** | 1. Add Book | Reads and validates the details, rejects a duplicate ISBN, assigns an auto-incrementing Book ID and adds the book to the `ArrayList`. |
| **Read** | 2. View All Books / 5. Search | Lists every book, or finds one by ISBN. Shows a friendly message if the inventory is empty or the book is not found. |
| **Update** | 3. Update Book | Finds the book by ISBN, shows its current details, then updates title, author and year. Invalid values leave the book unchanged. |
| **Delete** | 4. Delete Book | Finds the book by ISBN, asks for confirmation, removes it and reports success. |

## Validation Rules

- **Title / Author:** must not be empty or blank.
- **ISBN:** exactly 13 digits, or 10 characters where the last may be `X` (hyphens/spaces are removed first). Format only; the check digit is not verified.
- **Publication year:** a whole number from 1000 up to the current year.
- **Menu choice:** must be a whole number from 1 to 6.

## Future Enhancements

- Save and load books from a file (CSV/JSON) for persistent storage
- Search by title or author
- Add quantity, category and availability fields
- Member management and book issue/return tracking
- ISBN check-digit verification
- Unit tests with JUnit
- A graphical interface (JavaFX/Swing) or a database (JDBC)

## Testing

See [TESTING_CHECKLIST.md](TESTING_CHECKLIST.md) for the manual test cases and expected results.

## Author

Prepared for the Yuva Internship – Week 2 Task. Add your name here.
