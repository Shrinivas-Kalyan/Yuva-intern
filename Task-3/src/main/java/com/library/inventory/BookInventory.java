package com.library.inventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Business logic for the library inventory. Contains no console I/O so every
 * method can be unit tested directly.
 */
public class BookInventory {

    private final List<Book> books = new ArrayList<>();

    // ---------- CREATE ----------

    public Book addBook(String isbn, String title, String author, int publicationYear) {
        return addBook(new Book(isbn, title, author, publicationYear));
    }

    public Book addBook(Book book) {
        if (book == null) {
            throw new InvalidBookException("Book must not be null.");
        }
        if (indexOf(book.getIsbn()) >= 0) {
            throw new DuplicateBookException(book.getIsbn());
        }
        books.add(book);
        return book;
    }

    // ---------- READ ----------

    /** Returns a read-only snapshot of all books in insertion order. */
    public List<Book> getAllBooks() {
        return Collections.unmodifiableList(new ArrayList<>(books));
    }

    public Optional<Book> findByIsbn(String isbn) {
        int index = indexOf(BookValidator.normalizeIsbn(isbn));
        return index >= 0 ? Optional.of(books.get(index)) : Optional.empty();
    }

    public Book getBookByIsbn(String isbn) {
        return findByIsbn(isbn).orElseThrow(() -> new BookNotFoundException(isbn));
    }

    public boolean isEmpty() {
        return books.isEmpty();
    }

    public int size() {
        return books.size();
    }

    // ---------- UPDATE ----------

    /**
     * Replaces title, author and year of an existing book. All new values are
     * validated first, so a failed update never leaves a book half-modified.
     */
    public Book updateBook(String isbn, String newTitle, String newAuthor, int newYear) {
        String normalized = BookValidator.normalizeIsbn(isbn);
        int index = indexOf(normalized);
        if (index < 0) {
            throw new BookNotFoundException(isbn);
        }
        String title = BookValidator.validateTitle(newTitle);
        String author = BookValidator.validateAuthor(newAuthor);
        int year = BookValidator.validateYear(newYear);

        Book book = books.get(index);
        book.setTitle(title);
        book.setAuthor(author);
        book.setPublicationYear(year);
        return book;
    }

    public Book updateTitle(String isbn, String newTitle) {
        Book book = getBookByIsbn(isbn);
        return updateBook(isbn, newTitle, book.getAuthor(), book.getPublicationYear());
    }

    public Book updateAuthor(String isbn, String newAuthor) {
        Book book = getBookByIsbn(isbn);
        return updateBook(isbn, book.getTitle(), newAuthor, book.getPublicationYear());
    }

    public Book updatePublicationYear(String isbn, int newYear) {
        Book book = getBookByIsbn(isbn);
        return updateBook(isbn, book.getTitle(), book.getAuthor(), newYear);
    }

    // ---------- DELETE ----------

    /** Removes and returns the book with the given ISBN. */
    public Book deleteBook(String isbn) {
        int index = indexOf(BookValidator.normalizeIsbn(isbn));
        if (index < 0) {
            throw new BookNotFoundException(isbn);
        }
        return books.remove(index);
    }

    // ---------- helpers ----------

    /** Index of the book whose (already normalized) ISBN matches exactly, or -1. */
    private int indexOf(String normalizedIsbn) {
        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).getIsbn().equals(normalizedIsbn)) {
                return i;
            }
        }
        return -1;
    }
}
