package com.library.inventory;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Menu-driven console interface. Input and output streams are injected so the
 * menu can be driven by unit tests with scripted input.
 */
public class InventoryApp {

    private final BookInventory inventory;
    private final Scanner scanner;
    private final PrintStream out;

    public InventoryApp(BookInventory inventory, InputStream in, PrintStream out) {
        this.inventory = inventory;
        this.scanner = new Scanner(in);
        this.out = out;
    }

    public static void main(String[] args) {
        new InventoryApp(new BookInventory(), System.in, System.out).run();
    }

    /** Runs the menu loop until the user chooses Exit or input ends. */
    public void run() {
        out.println("=== Library Book Inventory Management System ===");
        boolean running = true;
        while (running) {
            printMenu();
            try {
                String choice = readLine("Enter your choice: ");
                running = handleChoice(choice);
            } catch (NoSuchElementException e) {
                out.println("Input ended. Exiting.");
                running = false;
            }
        }
    }

    /** @return false when the user chose to exit. */
    boolean handleChoice(String choice) {
        switch (choice.trim()) {
            case "1":
                addBook();
                break;
            case "2":
                viewAllBooks();
                break;
            case "3":
                searchBook();
                break;
            case "4":
                updateBook();
                break;
            case "5":
                deleteBook();
                break;
            case "6":
                out.println("Goodbye!");
                return false;
            default:
                out.println("Invalid choice. Please enter a number from 1 to 6.");
        }
        return true;
    }

    private void printMenu() {
        out.println();
        out.println("1. Add book");
        out.println("2. View all books");
        out.println("3. Search book by ISBN");
        out.println("4. Update book");
        out.println("5. Delete book");
        out.println("6. Exit");
    }

    private void addBook() {
        try {
            String isbn = readLine("ISBN: ");
            String title = readLine("Title: ");
            String author = readLine("Author: ");
            int year = readYear("Publication year: ");
            inventory.addBook(isbn, title, author, year);
            out.println("Book added successfully.");
        } catch (InvalidBookException | DuplicateBookException e) {
            out.println("Error: " + e.getMessage());
        }
    }

    private void viewAllBooks() {
        List<Book> books = inventory.getAllBooks();
        if (books.isEmpty()) {
            out.println("The inventory is empty.");
            return;
        }
        for (Book book : books) {
            out.println(book.toDisplayString());
        }
    }

    private void searchBook() {
        try {
            String isbn = readLine("ISBN to search: ");
            out.println(inventory.getBookByIsbn(isbn).toDisplayString());
        } catch (InvalidBookException | BookNotFoundException e) {
            out.println("Error: " + e.getMessage());
        }
    }

    private void updateBook() {
        try {
            String isbn = readLine("ISBN of book to update: ");
            String title = readLine("New title: ");
            String author = readLine("New author: ");
            int year = readYear("New publication year: ");
            inventory.updateBook(isbn, title, author, year);
            out.println("Book updated successfully.");
        } catch (InvalidBookException | BookNotFoundException e) {
            out.println("Error: " + e.getMessage());
        }
    }

    private void deleteBook() {
        try {
            String isbn = readLine("ISBN of book to delete: ");
            inventory.deleteBook(isbn);
            out.println("Book deleted successfully.");
        } catch (InvalidBookException | BookNotFoundException e) {
            out.println("Error: " + e.getMessage());
        }
    }

    private String readLine(String prompt) {
        out.print(prompt);
        return scanner.nextLine();
    }

    private int readYear(String prompt) {
        String text = readLine(prompt).trim();
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw new InvalidBookException("Publication year must be a number: " + text);
        }
    }
}
