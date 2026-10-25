package com.library.inventory;

import java.util.List;

/**
 * The operations the console menu needs from an inventory.
 *
 * <p>The menu ({@link InventoryApp}) depends on this interface instead of the
 * concrete {@link BookInventory} (Dependency Inversion Principle), so the
 * storage behind it can be replaced, for example by a file or database
 * implementation, without touching the menu code.
 */
public interface InventoryService {

    Book addBook(String isbn, String title, String author, int publicationYear);

    List<Book> getAllBooks();

    Book getBookByIsbn(String isbn);

    Book updateBook(String isbn, String newTitle, String newAuthor, int newYear);

    Book deleteBook(String isbn);
}
