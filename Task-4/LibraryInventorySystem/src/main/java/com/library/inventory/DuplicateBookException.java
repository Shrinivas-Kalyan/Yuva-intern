package com.library.inventory;

/** Thrown when adding a book whose ISBN already exists in the inventory. */
public class DuplicateBookException extends InventoryException {

    private static final long serialVersionUID = 1L;

    public DuplicateBookException(String isbn) {
        super("A book with ISBN " + isbn + " already exists.");
    }
}
