package com.library.inventory;

/**
 * Common parent of every application-specific error (invalid data, duplicate
 * ISBN, missing book). The console menu catches this one type instead of
 * repeating a list of catch clauses in every menu action.
 */
public abstract class InventoryException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    protected InventoryException(String message) {
        super(message);
    }
}
