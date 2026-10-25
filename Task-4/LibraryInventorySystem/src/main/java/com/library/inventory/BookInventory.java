package com.library.inventory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Business logic for the library inventory. Contains no console I/O so every
 * method can be unit tested directly.
 *
 * <p>Books are stored in a {@link LinkedHashMap} keyed by normalized ISBN:
 * <ul>
 *   <li>a map key is unique, so a duplicate ISBN can be detected without scanning;</li>
 *   <li>lookup, insert and delete by ISBN take average O(1) time instead of
 *       the O(n) scan an {@code ArrayList} needs;</li>
 *   <li>{@code LinkedHashMap} (not {@code HashMap}) keeps insertion order, so
 *       "view all books" still lists books in the order they were added.</li>
 * </ul>
 */
public class BookInventory implements InventoryService {

    private final Map<String, Book> booksByIsbn = new LinkedHashMap<>();

    // ---------- CREATE ----------

    @Override
    public Book addBook(String isbn, String title, String author, int publicationYear) {
        return addBook(new Book(isbn, title, author, publicationYear));
    }

    public Book addBook(Book book) {
        if (book == null) {
            throw new InvalidBookException("Book must not be null.");
        }
        // putIfAbsent checks for a duplicate and inserts in a single map operation.
        if (booksByIsbn.putIfAbsent(book.getIsbn(), book) != null) {
            throw new DuplicateBookException(book.getIsbn());
        }
        return book;
    }

    // ---------- READ ----------

    /** Returns a read-only snapshot of all books in insertion order. */
    @Override
    public List<Book> getAllBooks() {
        return List.copyOf(booksByIsbn.values());
    }

    public Optional<Book> findByIsbn(String isbn) {
        return Optional.ofNullable(booksByIsbn.get(BookValidator.normalizeIsbn(isbn)));
    }

    @Override
    public Book getBookByIsbn(String isbn) {
        return findByIsbn(isbn).orElseThrow(() -> new BookNotFoundException(isbn));
    }

    public boolean isEmpty() {
        return booksByIsbn.isEmpty();
    }

    public int size() {
        return booksByIsbn.size();
    }

    // ---------- UPDATE ----------

    /**
     * Replaces title, author and year of an existing book. The book is looked
     * up first (missing book -> BookNotFoundException); all new values are then
     * validated before anything is changed, so a failed update never leaves a
     * book half-modified.
     */
    @Override
    public Book updateBook(String isbn, String newTitle, String newAuthor, int newYear) {
        Book book = getBookByIsbn(isbn);
        book.updateDetails(newTitle, newAuthor, newYear);
        return book;
    }

    public Book updateTitle(String isbn, String newTitle) {
        Book book = getBookByIsbn(isbn);
        book.updateDetails(newTitle, book.getAuthor(), book.getPublicationYear());
        return book;
    }

    public Book updateAuthor(String isbn, String newAuthor) {
        Book book = getBookByIsbn(isbn);
        book.updateDetails(book.getTitle(), newAuthor, book.getPublicationYear());
        return book;
    }

    public Book updatePublicationYear(String isbn, int newYear) {
        Book book = getBookByIsbn(isbn);
        book.updateDetails(book.getTitle(), book.getAuthor(), newYear);
        return book;
    }

    // ---------- DELETE ----------

    /** Removes and returns the book with the given ISBN. */
    @Override
    public Book deleteBook(String isbn) {
        Book removed = booksByIsbn.remove(BookValidator.normalizeIsbn(isbn));
        if (removed == null) {
            throw new BookNotFoundException(isbn);
        }
        return removed;
    }
}
