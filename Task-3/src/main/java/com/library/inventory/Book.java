package com.library.inventory;

import java.util.Objects;

/** A single book record. All fields are validated when set. */
public class Book {

    private final String isbn;
    private String title;
    private String author;
    private int publicationYear;

    public Book(String isbn, String title, String author, int publicationYear) {
        this.isbn = BookValidator.normalizeIsbn(isbn);
        this.title = BookValidator.validateTitle(title);
        this.author = BookValidator.validateAuthor(author);
        this.publicationYear = BookValidator.validateYear(publicationYear);
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public void setTitle(String title) {
        this.title = BookValidator.validateTitle(title);
    }

    public void setAuthor(String author) {
        this.author = BookValidator.validateAuthor(author);
    }

    public void setPublicationYear(int publicationYear) {
        this.publicationYear = BookValidator.validateYear(publicationYear);
    }

    /** One-line, human readable representation used by the console menu. */
    public String toDisplayString() {
        return String.format("ISBN: %s | Title: %s | Author: %s | Year: %d",
                isbn, title, author, publicationYear);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Book)) {
            return false;
        }
        Book other = (Book) o;
        return isbn.equals(other.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }

    @Override
    public String toString() {
        return toDisplayString();
    }
}
