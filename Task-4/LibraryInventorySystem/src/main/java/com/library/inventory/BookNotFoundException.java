package com.library.inventory;

/** Thrown when no book with the requested ISBN exists in the inventory. */
public class BookNotFoundException extends InventoryException {

    private static final long serialVersionUID = 1L;

    public BookNotFoundException(String isbn) {
        super("No book found with ISBN: " + isbn);
    }
}
