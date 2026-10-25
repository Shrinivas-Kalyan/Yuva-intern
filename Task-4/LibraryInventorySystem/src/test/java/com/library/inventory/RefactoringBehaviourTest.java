package com.library.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Week 4: tests that pin down behaviour which the refactoring (ArrayList to
 * LinkedHashMap, shared exception base, table-driven menu) must preserve or
 * newly guarantees.
 */
@DisplayName("Week 4 refactoring behaviour")
class RefactoringBehaviourTest {

    private static final String ISBN_1 = "9780134685991";
    private static final String ISBN_2 = "9780132350884";
    private static final String ISBN_3 = "0306406152";

    private BookInventory inventory;

    @BeforeEach
    void setUp() {
        inventory = new BookInventory();
        inventory.addBook(ISBN_1, "Effective Java", "Joshua Bloch", 2018);
        inventory.addBook(ISBN_2, "Clean Code", "Robert C. Martin", 2008);
        inventory.addBook(ISBN_3, "Old Title", "Old Author", 1990);
    }

    private static List<String> isbns(BookInventory inv) {
        return inv.getAllBooks().stream().map(Book::getIsbn).toList();
    }

    @Test
    void deletingMiddleBook_keepsInsertionOrderOfTheRest() {
        inventory.deleteBook(ISBN_2);
        assertEquals(List.of(ISBN_1, ISBN_3), isbns(inventory));
    }

    @Test
    void updatingBook_doesNotChangeItsPositionInTheListing() {
        inventory.updateBook(ISBN_2, "Clean Code 2", "Uncle Bob", 2009);
        assertEquals(List.of(ISBN_1, ISBN_2, ISBN_3), isbns(inventory));
    }

    @Test
    void deletedIsbn_canBeAddedAgain_andGoesToTheEnd() {
        inventory.deleteBook(ISBN_1);
        inventory.addBook(ISBN_1, "Effective Java", "Joshua Bloch", 2018);

        assertEquals(List.of(ISBN_2, ISBN_3, ISBN_1), isbns(inventory));
    }

    @Test
    void isbnWithHyphens_findsTheSameBookAsPlainIsbn() {
        assertEquals(inventory.getBookByIsbn("978-0-13-468599-1"),
                inventory.getBookByIsbn(ISBN_1));
    }

    @Test
    void duplicateAdd_withDifferentFormat_isRejectedAndSizeUnchanged() {
        assertThrows(DuplicateBookException.class,
                () -> inventory.addBook("978-0-13-468599-1", "X", "Y", 2000));
        assertEquals(3, inventory.size());
    }

    @Test
    void updateDetails_withBadYear_leavesBookUnchanged() {
        Book book = inventory.getBookByIsbn(ISBN_3);

        assertThrows(InvalidBookException.class,
                () -> book.updateDetails("New Title", "New Author", 1000));

        assertEquals("Old Title", book.getTitle());
        assertEquals("Old Author", book.getAuthor());
        assertEquals(1990, book.getPublicationYear());
    }

    @Test
    void updateTitleAuthorAndYear_changeOnlyThatField() {
        inventory.updateTitle(ISBN_3, "T");
        inventory.updateAuthor(ISBN_3, "A");
        inventory.updatePublicationYear(ISBN_3, 2001);

        Book book = inventory.getBookByIsbn(ISBN_3);
        assertEquals("T", book.getTitle());
        assertEquals("A", book.getAuthor());
        assertEquals(2001, book.getPublicationYear());
    }

    @Test
    void allApplicationExceptions_shareTheInventoryExceptionBase() {
        assertInstanceOf(InventoryException.class, new InvalidBookException("x"));
        assertInstanceOf(InventoryException.class, new DuplicateBookException(ISBN_1));
        assertInstanceOf(InventoryException.class, new BookNotFoundException(ISBN_1));
    }

    @Test
    void validatorMessages_nameTheCorrectField() {
        assertEquals("Title must not be empty.",
                assertThrows(InvalidBookException.class,
                        () -> BookValidator.validateTitle(" ")).getMessage());
        assertEquals("Author must not be empty.",
                assertThrows(InvalidBookException.class,
                        () -> BookValidator.validateAuthor(null)).getMessage());
        assertEquals("ISBN must not be empty.",
                assertThrows(InvalidBookException.class,
                        () -> BookValidator.normalizeIsbn("")).getMessage());
    }

    @Test
    void menu_printsNumberedEntriesAndExit() {
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        new InventoryApp(inventory,
                new ByteArrayInputStream("6\n".getBytes(StandardCharsets.UTF_8)),
                new PrintStream(captured, true, StandardCharsets.UTF_8)).run();
        String output = captured.toString(StandardCharsets.UTF_8);

        assertTrue(output.contains("1. Add book"));
        assertTrue(output.contains("5. Delete book"));
        assertTrue(output.contains("6. Exit"));
    }

    @Test
    void menu_invalidChoiceMessage_mentionsTheValidRange() {
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        new InventoryApp(inventory,
                new ByteArrayInputStream("9\n6\n".getBytes(StandardCharsets.UTF_8)),
                new PrintStream(captured, true, StandardCharsets.UTF_8)).run();

        assertTrue(captured.toString(StandardCharsets.UTF_8)
                .contains("Invalid choice. Please enter a number from 1 to 6."));
    }

    @Test
    void menu_updateWithBadYear_showsErrorAndKeepsOldData() {
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        String script = String.join("\n", "4", ISBN_3, "New", "New", "1000", "6") + "\n";
        new InventoryApp(inventory,
                new ByteArrayInputStream(script.getBytes(StandardCharsets.UTF_8)),
                new PrintStream(captured, true, StandardCharsets.UTF_8)).run();

        assertTrue(captured.toString(StandardCharsets.UTF_8).contains("Error: Publication year must be"));
        assertEquals("Old Title", inventory.getBookByIsbn(ISBN_3).getTitle());
    }
}
