package com.library.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Drives the menu with scripted console input and checks the printed output. */
@DisplayName("InventoryApp (console menu)")
class InventoryAppTest {

    private BookInventory inventory;
    private ByteArrayOutputStream captured;

    @BeforeEach
    void setUp() {
        inventory = new BookInventory();
        captured = new ByteArrayOutputStream();
    }

    /** Runs the app with the given input lines and returns everything it printed. */
    private String runWithInput(String... lines) {
        String input = String.join("\n", lines) + "\n";
        InventoryApp app = new InventoryApp(inventory,
                new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)),
                new PrintStream(captured, true, StandardCharsets.UTF_8));
        app.run();
        return captured.toString(StandardCharsets.UTF_8);
    }

    @Test
    void exitChoice_endsProgramWithGoodbye() {
        String output = runWithInput("6");
        assertTrue(output.contains("Goodbye!"));
    }

    @Test
    void invalidMenuChoices_showMessageAndContinue() {
        String output = runWithInput("0", "7", "abc", "", "-1", "6");
        assertEquals(5, countOccurrences(output, "Invalid choice"));
        assertTrue(output.contains("Goodbye!"));
    }

    @Test
    void endOfInput_exitsGracefullyWithoutException() {
        String output = runWithInput("2"); // no "6": input just ends
        assertTrue(output.contains("Input ended. Exiting."));
    }

    @Test
    void addBookThroughMenu_storesBook() {
        String output = runWithInput("1", "9780134685991", "Effective Java",
                "Joshua Bloch", "2018", "6");

        assertTrue(output.contains("Book added successfully."));
        assertEquals(1, inventory.size());
    }

    @Test
    void addBookWithNonNumericYear_showsErrorAndStoresNothing() {
        String output = runWithInput("1", "9780134685991", "Effective Java",
                "Joshua Bloch", "twenty", "6");

        assertTrue(output.contains("Error: Publication year must be a number"));
        assertTrue(inventory.isEmpty());
    }

    @Test
    void addBookWithDuplicateIsbn_showsError() {
        inventory.addBook("9780134685991", "Effective Java", "Joshua Bloch", 2018);

        String output = runWithInput("1", "9780134685991", "Other", "Other", "2000", "6");

        assertTrue(output.contains("already exists"));
        assertEquals(1, inventory.size());
    }

    @Test
    void addBookWithEmptyTitle_showsError() {
        String output = runWithInput("1", "9780134685991", "", "Joshua Bloch", "2018", "6");
        assertTrue(output.contains("Error: Title must not be empty."));
        assertTrue(inventory.isEmpty());
    }

    @Test
    void viewAllOnEmptyInventory_showsEmptyMessage() {
        String output = runWithInput("2", "6");
        assertTrue(output.contains("The inventory is empty."));
    }

    @Test
    void viewAll_listsEveryBookWithDetails() {
        inventory.addBook("9780134685991", "Effective Java", "Joshua Bloch", 2018);
        inventory.addBook("9780132350884", "Clean Code", "Robert C. Martin", 2008);

        String output = runWithInput("2", "6");

        assertTrue(output.contains(
                "ISBN: 9780134685991 | Title: Effective Java | Author: Joshua Bloch | Year: 2018"));
        assertTrue(output.contains(
                "ISBN: 9780132350884 | Title: Clean Code | Author: Robert C. Martin | Year: 2008"));
    }

    @Test
    void searchExistingBook_showsDetails() {
        inventory.addBook("9780134685991", "Effective Java", "Joshua Bloch", 2018);
        String output = runWithInput("3", "9780134685991", "6");
        assertTrue(output.contains("Title: Effective Java"));
    }

    @Test
    void searchMissingBook_showsNotFoundError() {
        String output = runWithInput("3", "9780134685991", "6");
        assertTrue(output.contains("Error: No book found with ISBN"));
    }

    @Test
    void searchInvalidIsbn_showsValidationError() {
        String output = runWithInput("3", "abc", "6");
        assertTrue(output.contains("Error: ISBN must be 10 or 13 digits"));
    }

    @Test
    void updateThroughMenu_changesBook() {
        inventory.addBook("9780134685991", "Effective Java", "Joshua Bloch", 2018);

        String output = runWithInput("4", "9780134685991", "Effective Java 3E",
                "J. Bloch", "2019", "6");

        assertTrue(output.contains("Book updated successfully."));
        Book book = inventory.getBookByIsbn("9780134685991");
        assertEquals("Effective Java 3E", book.getTitle());
        assertEquals("J. Bloch", book.getAuthor());
        assertEquals(2019, book.getPublicationYear());
    }

    @Test
    void updateMissingBook_showsError() {
        String output = runWithInput("4", "9780134685991", "T", "A", "2000", "6");
        assertTrue(output.contains("Error: No book found with ISBN"));
    }

    @Test
    void deleteThroughMenu_removesBook() {
        inventory.addBook("9780134685991", "Effective Java", "Joshua Bloch", 2018);

        String output = runWithInput("5", "9780134685991", "6");

        assertTrue(output.contains("Book deleted successfully."));
        assertTrue(inventory.isEmpty());
    }

    @Test
    void deleteMissingBook_showsError() {
        String output = runWithInput("5", "9780134685991", "6");
        assertTrue(output.contains("Error: No book found with ISBN"));
    }

    @Test
    void fullSessionOfConsecutiveOperations_worksEndToEnd() {
        String output = runWithInput(
                "1", "9780134685991", "Effective Java", "Joshua Bloch", "2018",
                "1", "9780132350884", "Clean Code", "Robert C. Martin", "2008",
                "4", "9780132350884", "Clean Code 2", "Uncle Bob", "2009",
                "5", "9780134685991",
                "2",
                "6");

        assertEquals(1, inventory.size());
        assertTrue(output.contains("Title: Clean Code 2"));
        assertFalse(output.contains("Title: Effective Java"));
    }

    private static int countOccurrences(String text, String part) {
        int count = 0;
        int from = 0;
        while ((from = text.indexOf(part, from)) >= 0) {
            count++;
            from += part.length();
        }
        return count;
    }
}
