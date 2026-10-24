import java.time.Year;

/**
 * Book represents a single book in the library inventory.
 * All fields are private (encapsulation) and validated before they are stored.
 */
public final class Book {

    private static final int MIN_YEAR = 1000;

    private int bookId;
    private String title;
    private String author;
    private String isbn;
    private int publicationYear;

    /**
     * Creates a new Book. Throws IllegalArgumentException if any value is invalid.
     */
    public Book(int bookId, String title, String author, String isbn, int publicationYear) {
        setBookId(bookId);
        setTitle(title);
        setAuthor(author);
        setIsbn(isbn);
        setPublicationYear(publicationYear);
    }

    // ---------- Getters ----------

    public int getBookId() {
        return bookId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getIsbn() {
        return isbn;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    // ---------- Setters (with validation) ----------

    public void setBookId(int bookId) {
        if (bookId <= 0) {
            throw new IllegalArgumentException("Book ID must be a positive number.");
        }
        this.bookId = bookId;
    }

    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Book title cannot be empty.");
        }
        this.title = title.trim();
    }

    public void setAuthor(String author) {
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("Author name cannot be empty.");
        }
        this.author = author.trim();
    }

    public void setIsbn(String isbn) {
        String cleaned = normalizeIsbn(isbn);
        if (!isValidIsbnFormat(cleaned)) {
            throw new IllegalArgumentException(
                    "Invalid ISBN. It must contain exactly 10 or 13 digits "
                    + "(hyphens and spaces are allowed; ISBN-10 may end with 'X').");
        }
        this.isbn = cleaned;
    }

    public void setPublicationYear(int publicationYear) {
        int currentYear = Year.now().getValue();
        if (publicationYear < MIN_YEAR || publicationYear > currentYear) {
            throw new IllegalArgumentException(
                    "Publication year must be between " + MIN_YEAR + " and " + currentYear + ".");
        }
        this.publicationYear = publicationYear;
    }

    // ---------- Helper methods ----------

    /**
     * Removes hyphens and spaces from an ISBN and converts it to upper case,
     * so "978-0-13-468599-1" and "9780134685991" are treated as the same ISBN.
     */
    public static String normalizeIsbn(String isbn) {
        if (isbn == null) {
            return "";
        }
        return isbn.replace("-", "").replace(" ", "").trim().toUpperCase();
    }

    /**
     * Checks that a (normalized) ISBN has 13 digits, or 10 characters
     * where the last one may be the letter X.
     */
    public static boolean isValidIsbnFormat(String cleanedIsbn) {
        return cleanedIsbn.matches("\\d{13}") || cleanedIsbn.matches("\\d{9}[\\dX]");
    }

    /**
     * Prints the book's details in a readable format.
     */
    public void displayBookInfo() {
        System.out.println("--------------------------------------------");
        System.out.println("Book ID          : " + bookId);
        System.out.println("Title            : " + title);
        System.out.println("Author           : " + author);
        System.out.println("ISBN             : " + isbn);
        System.out.println("Publication Year : " + publicationYear);
        System.out.println("--------------------------------------------");
    }
}
