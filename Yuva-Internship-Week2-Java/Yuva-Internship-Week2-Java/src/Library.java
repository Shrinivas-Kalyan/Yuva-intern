import java.util.ArrayList;

/**
 * Library manages the book inventory in memory using an ArrayList.
 * It contains the CRUD logic; it does not read any user input itself.
 */
public class Library {

    private final ArrayList<Book> books = new ArrayList<>();
    private int nextBookId = 1; // Used to auto-generate unique Book IDs

    // ---------- CREATE ----------

    /**
     * Adds a new book. Throws IllegalArgumentException if the data is invalid
     * or if a book with the same ISBN already exists.
     */
    public Book addBook(String title, String author, String isbn, int publicationYear) {
        // Creating the Book validates every field
        Book newBook = new Book(nextBookId, title, author, isbn, publicationYear);

        // Prevent duplicate ISBN numbers
        if (findBookByIsbn(newBook.getIsbn()) != null) {
            throw new IllegalArgumentException(
                    "A book with ISBN " + newBook.getIsbn() + " already exists.");
        }

        books.add(newBook);
        nextBookId++; // Only increase the ID when the book was really added
        return newBook;
    }

    // ---------- READ ----------

    /**
     * Returns a copy of the list so outside code cannot change the inventory directly.
     */
    public ArrayList<Book> getAllBooks() {
        return new ArrayList<>(books);
    }

    /**
     * Searches for a book by ISBN. Returns null when no book matches.
     */
    public Book findBookByIsbn(String isbn) {
        String cleaned = Book.normalizeIsbn(isbn);
        for (Book book : books) {
            if (book.getIsbn().equals(cleaned)) {
                return book;
            }
        }
        return null;
    }

    public boolean isEmpty() {
        return books.isEmpty();
    }

    // ---------- UPDATE ----------

    /**
     * Updates the title, author and publication year of the book with the given ISBN.
     * Returns true if the book was found and updated, false if it was not found.
     * Throws IllegalArgumentException if a new value is invalid; in that case
     * the original book is left unchanged.
     */
    public boolean updateBook(String isbn, String newTitle, String newAuthor, int newYear) {
        Book book = findBookByIsbn(isbn);
        if (book == null) {
            return false;
        }

        // Validate by building a temporary Book first, so a bad value
        // can never leave the real book half-updated.
        new Book(book.getBookId(), newTitle, newAuthor, book.getIsbn(), newYear);

        book.setTitle(newTitle);
        book.setAuthor(newAuthor);
        book.setPublicationYear(newYear);
        return true;
    }

    // ---------- DELETE ----------

    /**
     * Deletes the book with the given ISBN.
     * Returns true if a book was deleted, false if no book matched.
     */
    public boolean deleteBook(String isbn) {
        Book book = findBookByIsbn(isbn);
        if (book == null) {
            return false;
        }
        books.remove(book);
        return true;
    }
}
