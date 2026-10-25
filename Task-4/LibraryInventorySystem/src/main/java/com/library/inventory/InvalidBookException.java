package com.library.inventory;

/** Thrown when book data (ISBN, title, author, year) fails validation. */
public class InvalidBookException extends InventoryException {

    private static final long serialVersionUID = 1L;

    public InvalidBookException(String message) {
        super(message);
    }
}
