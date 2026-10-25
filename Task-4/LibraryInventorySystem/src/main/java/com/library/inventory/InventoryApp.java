package com.library.inventory;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Menu-driven console interface (application entry point). Input and output
 * streams are injected so the menu can be driven by unit tests with scripted input.
 *
 * <p>Menu entries live in one table. The printed menu, the dispatch of the
 * user's choice and the "invalid choice" hint are all generated from it, so a
 * new feature is added by registering one more entry (Open/Closed Principle).
 */
public class InventoryApp {

    private static final String EXIT_CHOICE_LABEL = "Exit";

    /** Prompt wording differs between adding a new book and updating an existing one. */
    private record Prompts(String title, String author, String year) { }

    private static final Prompts ADD_PROMPTS =
            new Prompts("Title: ", "Author: ", "Publication year: ");
    private static final Prompts UPDATE_PROMPTS =
            new Prompts("New title: ", "New author: ", "New publication year: ");

    private record MenuItem(String label, Runnable action) { }

    private final InventoryService inventory;
    private final InputHandler input;
    private final PrintStream out;
    private final Map<String, MenuItem> menu = new LinkedHashMap<>();

    public InventoryApp(InventoryService inventory, InputStream in, PrintStream out) {
        this.inventory = inventory;
        this.input = new InputHandler(in, out);
        this.out = out;
        registerMenuItems();
    }

    public static void main(String[] args) {
        new InventoryApp(new BookInventory(), System.in, System.out).run();
    }

    /** Menu numbers are assigned in registration order; "Exit" is always the last number. */
    private void registerMenuItems() {
        addMenuItem("Add book", this::addBook);
        addMenuItem("View all books", this::viewAllBooks);
        addMenuItem("Search book by ISBN", this::searchBook);
        addMenuItem("Update book", this::updateBook);
        addMenuItem("Delete book", this::deleteBook);
    }

    private void addMenuItem(String label, Runnable action) {
        menu.put(String.valueOf(menu.size() + 1), new MenuItem(label, action));
    }

    private String exitChoice() {
        return String.valueOf(menu.size() + 1);
    }

    /** Runs the menu loop until the user chooses Exit or input ends. */
    public void run() {
        out.println("=== Library Book Inventory Management System ===");
        boolean running = true;
        while (running) {
            printMenu();
            try {
                running = handleChoice(input.readLine("Enter your choice: "));
            } catch (NoSuchElementException e) {
                out.println("Input ended. Exiting.");
                running = false;
            }
        }
    }

    /** @return false when the user chose to exit. */
    boolean handleChoice(String choice) {
        String key = choice.trim();
        if (key.equals(exitChoice())) {
            out.println("Goodbye!");
            return false;
        }
        MenuItem item = menu.get(key);
        if (item == null) {
            out.println("Invalid choice. Please enter a number from 1 to " + exitChoice() + ".");
        } else {
            runSafely(item.action());
        }
        return true;
    }

    private void printMenu() {
        out.println();
        menu.forEach((number, item) -> out.println(number + ". " + item.label()));
        out.println(exitChoice() + ". " + EXIT_CHOICE_LABEL);
    }

    /**
     * Single place that turns expected application errors (invalid input,
     * duplicate ISBN, missing book) into a friendly message. Previously every
     * menu action repeated its own try/catch with the same "Error: " output.
     * NoSuchElementException (end of input) is deliberately not caught here;
     * {@link #run()} handles it.
     */
    private void runSafely(Runnable action) {
        try {
            action.run();
        } catch (InventoryException e) {
            out.println("Error: " + e.getMessage());
        }
    }

    private void addBook() {
        String isbn = input.readLine("ISBN: ");
        BookDetails details = readBookDetails(ADD_PROMPTS);
        inventory.addBook(isbn, details.title(), details.author(), details.year());
        out.println("Book added successfully.");
    }

    private void viewAllBooks() {
        List<Book> books = inventory.getAllBooks();
        if (books.isEmpty()) {
            out.println("The inventory is empty.");
            return;
        }
        books.forEach(book -> out.println(book.toDisplayString()));
    }

    private void searchBook() {
        String isbn = input.readLine("ISBN to search: ");
        out.println(inventory.getBookByIsbn(isbn).toDisplayString());
    }

    private void updateBook() {
        String isbn = input.readLine("ISBN of book to update: ");
        BookDetails details = readBookDetails(UPDATE_PROMPTS);
        inventory.updateBook(isbn, details.title(), details.author(), details.year());
        out.println("Book updated successfully.");
    }

    private void deleteBook() {
        String isbn = input.readLine("ISBN of book to delete: ");
        inventory.deleteBook(isbn);
        out.println("Book deleted successfully.");
    }

    private record BookDetails(String title, String author, int year) { }

    /** Add and update ask for the same three values; only the wording differs. */
    private BookDetails readBookDetails(Prompts prompts) {
        String title = input.readLine(prompts.title());
        String author = input.readLine(prompts.author());
        int year = input.readYear(prompts.year());
        return new BookDetails(title, author, year);
    }
}
