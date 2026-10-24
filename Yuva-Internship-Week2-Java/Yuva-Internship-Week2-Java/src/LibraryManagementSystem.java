import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * LibraryManagementSystem is the main class. It shows the menu,
 * reads user input with Scanner and calls the Library methods.
 */
public class LibraryManagementSystem {

    private static final Scanner scanner = new Scanner(System.in);
    private static final Library library = new Library();

    public static void main(String[] args) {
        boolean running = true;

        try {
            while (running) {
                printMenu();
                int choice = readInt("Enter your choice: ");

                switch (choice) {
                    case 1:
                        addBook();
                        break;
                    case 2:
                        viewAllBooks();
                        break;
                    case 3:
                        updateBook();
                        break;
                    case 4:
                        deleteBook();
                        break;
                    case 5:
                        searchBook();
                        break;
                    case 6:
                        running = false;
                        System.out.println("Thank you for using the Library Book Inventory "
                                + "Management System. Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter a number from 1 to 6.");
                }
            }
        } catch (NoSuchElementException e) {
            // Happens if input ends unexpectedly (for example Ctrl+D / Ctrl+Z)
            System.out.println("\nInput closed. Exiting the application.");
        } finally {
            scanner.close();
        }
    }

    // ---------- Menu ----------

    private static void printMenu() {
        System.out.println();
        System.out.println("===== LIBRARY BOOK INVENTORY MANAGEMENT SYSTEM =====");
        System.out.println();
        System.out.println("1. Add Book");
        System.out.println("2. View All Books");
        System.out.println("3. Update Book");
        System.out.println("4. Delete Book");
        System.out.println("5. Search Book by ISBN");
        System.out.println("6. Exit");
        System.out.println();
    }

    // ---------- CRUD operations ----------

    /** CREATE: asks for book details and adds the book to the inventory. */
    private static void addBook() {
        System.out.println("\n--- Add New Book ---");
        String title = readNonEmptyString("Enter book title: ", "Title");
        String author = readNonEmptyString("Enter author name: ", "Author name");
        String isbn = readValidIsbn("Enter ISBN (10 or 13 digits): ");
        int year = readInt("Enter publication year: ");

        try {
            Book added = library.addBook(title, author, isbn, year);
            System.out.println("Book added successfully! Assigned Book ID: " + added.getBookId());
        } catch (IllegalArgumentException e) {
            // Covers invalid year and duplicate ISBN
            System.out.println("Error: " + e.getMessage());
        }
    }

    /** READ: shows every book in the inventory. */
    private static void viewAllBooks() {
        System.out.println("\n--- All Books ---");
        if (library.isEmpty()) {
            System.out.println("No books are available in the inventory.");
            return;
        }

        ArrayList<Book> allBooks = library.getAllBooks();
        for (Book book : allBooks) {
            book.displayBookInfo();
        }
        System.out.println("Total books: " + allBooks.size());
    }

    /** UPDATE: finds a book by ISBN and changes title, author and year. */
    private static void updateBook() {
        System.out.println("\n--- Update Book ---");
        String isbn = readValidIsbn("Enter the ISBN of the book to update: ");

        Book book = library.findBookByIsbn(isbn);
        if (book == null) {
            System.out.println("Book not found. No book with ISBN " + Book.normalizeIsbn(isbn) + ".");
            return;
        }

        System.out.println("Current details:");
        book.displayBookInfo();
        System.out.println("(Press Enter to keep the current value for title and author.)");

        String newTitle = readOptionalString("Enter new title [" + book.getTitle() + "]: ", book.getTitle());
        String newAuthor = readOptionalString("Enter new author [" + book.getAuthor() + "]: ", book.getAuthor());
        int newYear = readInt("Enter new publication year [" + book.getPublicationYear() + "]: ");

        try {
            library.updateBook(isbn, newTitle, newAuthor, newYear);
            System.out.println("Book updated successfully!");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage() + " The book was not changed.");
        }
    }

    /** DELETE: removes a book by ISBN after the user confirms. */
    private static void deleteBook() {
        System.out.println("\n--- Delete Book ---");
        String isbn = readValidIsbn("Enter the ISBN of the book to delete: ");

        Book book = library.findBookByIsbn(isbn);
        if (book == null) {
            System.out.println("Book not found. No book with ISBN " + Book.normalizeIsbn(isbn) + ".");
            return;
        }

        book.displayBookInfo();
        System.out.print("Are you sure you want to delete this book? (y/n): ");
        String answer = scanner.nextLine().trim();

        if (answer.equalsIgnoreCase("y") || answer.equalsIgnoreCase("yes")) {
            if (library.deleteBook(isbn)) {
                System.out.println("Book deleted successfully!");
            } else {
                System.out.println("Deletion failed. The book could not be found.");
            }
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    /** SEARCH: shows one book by its ISBN. */
    private static void searchBook() {
        System.out.println("\n--- Search Book by ISBN ---");
        String isbn = readValidIsbn("Enter the ISBN to search: ");

        Book book = library.findBookByIsbn(isbn);
        if (book == null) {
            System.out.println("Book not found. No book with ISBN " + Book.normalizeIsbn(isbn) + ".");
        } else {
            System.out.println("Book found:");
            book.displayBookInfo();
        }
    }

    // ---------- Input helper methods ----------

    /** Keeps asking until the user types a whole number. */
    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a whole number.");
            }
        }
    }

    /** Keeps asking until the user types something that is not blank. */
    private static String readNonEmptyString(String prompt, String fieldName) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println(fieldName + " cannot be empty. Please try again.");
        }
    }

    /** Returns the typed text, or the default value if the user just presses Enter. */
    private static String readOptionalString(String prompt, String defaultValue) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        return input.isEmpty() ? defaultValue : input;
    }

    /** Keeps asking until the user types an ISBN with a valid format. */
    private static String readValidIsbn(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = Book.normalizeIsbn(scanner.nextLine());
            if (Book.isValidIsbnFormat(input)) {
                return input;
            }
            System.out.println("Invalid ISBN. Enter exactly 10 or 13 digits "
                    + "(hyphens allowed; ISBN-10 may end with X).");
        }
    }
}
