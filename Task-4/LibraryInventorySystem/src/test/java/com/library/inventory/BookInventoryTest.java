package com.library.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link BookInventory}. A fresh inventory is created before every
 * test, so tests are independent and can run in any order.
 */
@DisplayName("BookInventory")
class BookInventoryTest {

    private static final String ISBN_1 = "9780134685991";
    private static final String ISBN_2 = "9780132350884";
    private static final String ISBN_3 = "0596009208";

    private BookInventory inventory;

    @BeforeEach
    void setUp() {
        inventory = new BookInventory();
    }

    @AfterEach
    void tearDown() {
        inventory = null;
    }

    /** Adds the three standard books used by the read/update/delete tests. */
    private void addSampleBooks() {
        inventory.addBook(ISBN_1, "Effective Java", "Joshua Bloch", 2018);
        inventory.addBook(ISBN_2, "Clean Code", "Robert C. Martin", 2008);
        inventory.addBook(ISBN_3, "Head First Java", "Kathy Sierra", 2005);
    }

    // ------------------------------------------------------------------
    @Nested
    @DisplayName("Create / add book")
    class CreateTests {

        @Test
        void addValidBook_isStored() {
            Book added = inventory.addBook(ISBN_1, "Effective Java", "Joshua Bloch", 2018);

            assertNotNull(added);
            assertEquals(1, inventory.size());
            assertEquals("Effective Java", inventory.getBookByIsbn(ISBN_1).getTitle());
        }

        @Test
        void addMultipleBooks_allStoredInInsertionOrder() {
            addSampleBooks();

            List<Book> all = inventory.getAllBooks();
            assertEquals(3, all.size());
            assertEquals(ISBN_1, all.get(0).getIsbn());
            assertEquals(ISBN_2, all.get(1).getIsbn());
            assertEquals(ISBN_3, all.get(2).getIsbn());
        }

        @Test
        void addBookWithExistingIsbn_throwsDuplicateAndKeepsOriginal() {
            inventory.addBook(ISBN_1, "Effective Java", "Joshua Bloch", 2018);

            assertThrows(DuplicateBookException.class,
                    () -> inventory.addBook(ISBN_1, "Other Title", "Other Author", 2000));

            assertEquals(1, inventory.size());
            assertEquals("Effective Java", inventory.getBookByIsbn(ISBN_1).getTitle());
        }

        @Test
        void addBookWithSameIsbnInDifferentFormat_isDuplicate() {
            inventory.addBook("9780134685991", "Effective Java", "Joshua Bloch", 2018);

            assertThrows(DuplicateBookException.class,
                    () -> inventory.addBook("978-0-13-468599-1", "Copy", "Someone", 2018));
            assertEquals(1, inventory.size());
        }

        @Test
        void addBookWithEmptyTitle_throwsAndInventoryUnchanged() {
            assertThrows(InvalidBookException.class,
                    () -> inventory.addBook(ISBN_1, "", "Joshua Bloch", 2018));
            assertTrue(inventory.isEmpty());
        }

        @Test
        void addBookWithEmptyAuthor_throwsAndInventoryUnchanged() {
            assertThrows(InvalidBookException.class,
                    () -> inventory.addBook(ISBN_1, "Effective Java", "  ", 2018));
            assertTrue(inventory.isEmpty());
        }

        @Test
        void addBookWithInvalidIsbn_throwsAndInventoryUnchanged() {
            assertThrows(InvalidBookException.class,
                    () -> inventory.addBook("123", "Effective Java", "Joshua Bloch", 2018));
            assertThrows(InvalidBookException.class,
                    () -> inventory.addBook("not-an-isbn", "Effective Java", "Joshua Bloch", 2018));
            assertTrue(inventory.isEmpty());
        }

        @Test
        void addBookWithInvalidYear_throwsAndInventoryUnchanged() {
            assertThrows(InvalidBookException.class,
                    () -> inventory.addBook(ISBN_1, "Effective Java", "Joshua Bloch", 1000));
            assertThrows(InvalidBookException.class,
                    () -> inventory.addBook(ISBN_1, "Effective Java", "Joshua Bloch", -5));
            assertTrue(inventory.isEmpty());
        }

        @Test
        void addBookWithFutureYear_isRejected() {
            int nextYear = BookValidator.maxYear() + 1;
            assertThrows(InvalidBookException.class,
                    () -> inventory.addBook(ISBN_1, "Future Book", "Time Traveller", nextYear));
            assertTrue(inventory.isEmpty());
        }

        @Test
        void addNullBook_throwsInvalidBookException() {
            assertThrows(InvalidBookException.class, () -> inventory.addBook((Book) null));
        }

        @Test
        void addBookWithNullFields_throwsInvalidBookException() {
            assertThrows(InvalidBookException.class,
                    () -> inventory.addBook(null, "Title", "Author", 2000));
            assertThrows(InvalidBookException.class,
                    () -> inventory.addBook(ISBN_1, null, "Author", 2000));
            assertThrows(InvalidBookException.class,
                    () -> inventory.addBook(ISBN_1, "Title", null, 2000));
        }
    }

    // ------------------------------------------------------------------
    @Nested
    @DisplayName("Read / view and search")
    class ReadTests {

        @Test
        void getAllBooks_returnsEveryBook() {
            addSampleBooks();
            assertEquals(3, inventory.getAllBooks().size());
        }

        @Test
        void getAllBooks_onEmptyInventory_returnsEmptyList() {
            assertTrue(inventory.getAllBooks().isEmpty());
            assertTrue(inventory.isEmpty());
            assertEquals(0, inventory.size());
        }

        @Test
        void getAllBooks_returnedListCannotModifyInventory() {
            addSampleBooks();
            List<Book> snapshot = inventory.getAllBooks();

            assertThrows(UnsupportedOperationException.class, snapshot::clear);
            assertEquals(3, inventory.size());
        }

        @Test
        void findExistingBook_returnsIt() {
            addSampleBooks();

            Optional<Book> found = inventory.findByIsbn(ISBN_2);
            assertTrue(found.isPresent());
            assertEquals("Clean Code", found.get().getTitle());
        }

        @Test
        void findNonexistentBook_returnsEmptyOptional() {
            addSampleBooks();
            assertFalse(inventory.findByIsbn("9781234567897").isPresent());
        }

        @Test
        void getBookByIsbnNonexistent_throwsBookNotFound() {
            addSampleBooks();
            assertThrows(BookNotFoundException.class,
                    () -> inventory.getBookByIsbn("9781234567897"));
        }

        @Test
        void findOnEmptyInventory_returnsEmptyOptional() {
            assertFalse(inventory.findByIsbn(ISBN_1).isPresent());
        }

        @Test
        void searchWithInvalidIsbn_throwsInvalidBook() {
            addSampleBooks();
            assertThrows(InvalidBookException.class, () -> inventory.findByIsbn("abc"));
            assertThrows(InvalidBookException.class, () -> inventory.findByIsbn(""));
            assertThrows(InvalidBookException.class, () -> inventory.findByIsbn(null));
        }

        @Test
        void searchWithPartialIsbn_doesNotMatch() {
            addSampleBooks();
            // 10-digit prefix of ISBN_1 is a *valid* ISBN format but a different book
            assertFalse(inventory.findByIsbn("9780134685").isPresent());
        }

        @Test
        void searchAcceptsHyphenatedIsbn() {
            addSampleBooks();
            assertTrue(inventory.findByIsbn("978-0-13-468599-1").isPresent());
        }

        @Test
        void bookDetails_areDisplayedCorrectly() {
            inventory.addBook(ISBN_1, "Effective Java", "Joshua Bloch", 2018);

            assertEquals(
                    "ISBN: 9780134685991 | Title: Effective Java | Author: Joshua Bloch | Year: 2018",
                    inventory.getBookByIsbn(ISBN_1).toDisplayString());
        }
    }

    // ------------------------------------------------------------------
    @Nested
    @DisplayName("Update book")
    class UpdateTests {

        @Test
        void updateTitle_isStored() {
            addSampleBooks();
            inventory.updateTitle(ISBN_1, "Effective Java 3rd Edition");

            Book book = inventory.getBookByIsbn(ISBN_1);
            assertEquals("Effective Java 3rd Edition", book.getTitle());
            assertEquals("Joshua Bloch", book.getAuthor());
            assertEquals(2018, book.getPublicationYear());
        }

        @Test
        void updateAuthor_isStored() {
            addSampleBooks();
            inventory.updateAuthor(ISBN_2, "Uncle Bob");

            Book book = inventory.getBookByIsbn(ISBN_2);
            assertEquals("Uncle Bob", book.getAuthor());
            assertEquals("Clean Code", book.getTitle());
        }

        @Test
        void updatePublicationYear_isStored() {
            addSampleBooks();
            inventory.updatePublicationYear(ISBN_3, 2006);
            assertEquals(2006, inventory.getBookByIsbn(ISBN_3).getPublicationYear());
        }

        @Test
        void updateAllFieldsWithValidIsbn_storesEverything() {
            addSampleBooks();
            inventory.updateBook(ISBN_1, "New Title", "New Author", 2020);

            Book book = inventory.getBookByIsbn(ISBN_1);
            assertEquals("New Title", book.getTitle());
            assertEquals("New Author", book.getAuthor());
            assertEquals(2020, book.getPublicationYear());
            assertEquals(ISBN_1, book.getIsbn());
        }

        @Test
        void updateDoesNotChangeInventorySize() {
            addSampleBooks();
            inventory.updateBook(ISBN_1, "New Title", "New Author", 2020);
            assertEquals(3, inventory.size());
        }

        @Test
        void updateModifiesOnlyTheTargetBook() {
            addSampleBooks();
            inventory.updateBook(ISBN_2, "Changed", "Changed Author", 2010);

            Book first = inventory.getBookByIsbn(ISBN_1);
            Book third = inventory.getBookByIsbn(ISBN_3);
            assertEquals("Effective Java", first.getTitle());
            assertEquals("Joshua Bloch", first.getAuthor());
            assertEquals(2018, first.getPublicationYear());
            assertEquals("Head First Java", third.getTitle());
            assertEquals("Kathy Sierra", third.getAuthor());
            assertEquals(2005, third.getPublicationYear());
        }

        @Test
        void updateNonexistentBook_throwsBookNotFound() {
            addSampleBooks();
            assertThrows(BookNotFoundException.class,
                    () -> inventory.updateBook("9781234567897", "T", "A", 2000));
        }

        @Test
        void updateOnEmptyInventory_throwsBookNotFound() {
            assertThrows(BookNotFoundException.class,
                    () -> inventory.updateTitle(ISBN_1, "Title"));
        }

        @Test
        void updateWithInvalidInput_throwsAndLeavesBookUnchanged() {
            addSampleBooks();

            assertThrows(InvalidBookException.class,
                    () -> inventory.updateBook(ISBN_1, "", "Author", 2000));
            assertThrows(InvalidBookException.class,
                    () -> inventory.updateBook(ISBN_1, "Title", "", 2000));
            assertThrows(InvalidBookException.class,
                    () -> inventory.updateBook(ISBN_1, "Title", "Author", 99999));

            Book book = inventory.getBookByIsbn(ISBN_1);
            assertEquals("Effective Java", book.getTitle());
            assertEquals("Joshua Bloch", book.getAuthor());
            assertEquals(2018, book.getPublicationYear());
        }

        @Test
        void updateWithPartiallyInvalidInput_isAtomic() {
            addSampleBooks();
            // valid title but invalid year: title must NOT be applied
            assertThrows(InvalidBookException.class,
                    () -> inventory.updateBook(ISBN_1, "Brand New Title", "Joshua Bloch", 1000));
            assertEquals("Effective Java", inventory.getBookByIsbn(ISBN_1).getTitle());
        }

        @Test
        void updateWithInvalidIsbn_throwsInvalidBook() {
            addSampleBooks();
            assertThrows(InvalidBookException.class,
                    () -> inventory.updateBook("bad", "T", "A", 2000));
        }
    }

    // ------------------------------------------------------------------
    @Nested
    @DisplayName("Delete book")
    class DeleteTests {

        @Test
        void deleteExistingBook_removesIt() {
            addSampleBooks();

            Book removed = inventory.deleteBook(ISBN_2);

            assertEquals(ISBN_2, removed.getIsbn());
            assertEquals(2, inventory.size());
        }

        @Test
        void deletedBook_nolongerExists() {
            addSampleBooks();
            inventory.deleteBook(ISBN_2);

            assertFalse(inventory.findByIsbn(ISBN_2).isPresent());
            assertThrows(BookNotFoundException.class, () -> inventory.getBookByIsbn(ISBN_2));
        }

        @Test
        void deleteLeavesUnrelatedBooksUnchanged() {
            addSampleBooks();
            Book first = inventory.getBookByIsbn(ISBN_1);
            Book third = inventory.getBookByIsbn(ISBN_3);

            inventory.deleteBook(ISBN_2);

            assertSame(first, inventory.getBookByIsbn(ISBN_1));
            assertSame(third, inventory.getBookByIsbn(ISBN_3));
            assertEquals("Effective Java", first.getTitle());
            assertEquals("Head First Java", third.getTitle());
            assertEquals(List.of(ISBN_1, ISBN_3),
                    List.of(inventory.getAllBooks().get(0).getIsbn(),
                            inventory.getAllBooks().get(1).getIsbn()));
        }

        @Test
        void deleteNonexistentBook_throwsAndKeepsAllBooks() {
            addSampleBooks();
            assertThrows(BookNotFoundException.class,
                    () -> inventory.deleteBook("9781234567897"));
            assertEquals(3, inventory.size());
        }

        @Test
        void deleteWithInvalidIsbn_throwsInvalidBookAndKeepsAllBooks() {
            addSampleBooks();
            assertThrows(InvalidBookException.class, () -> inventory.deleteBook("xyz"));
            assertThrows(InvalidBookException.class, () -> inventory.deleteBook(null));
            assertEquals(3, inventory.size());
        }

        @Test
        void deleteFromEmptyInventory_throwsBookNotFound() {
            assertThrows(BookNotFoundException.class, () -> inventory.deleteBook(ISBN_1));
        }

        @Test
        void deleteSameBookTwice_secondAttemptFails() {
            addSampleBooks();
            inventory.deleteBook(ISBN_1);
            assertThrows(BookNotFoundException.class, () -> inventory.deleteBook(ISBN_1));
            assertEquals(2, inventory.size());
        }

        @Test
        void deleteAllBooks_leavesEmptyInventory() {
            addSampleBooks();
            inventory.deleteBook(ISBN_1);
            inventory.deleteBook(ISBN_2);
            inventory.deleteBook(ISBN_3);
            assertTrue(inventory.isEmpty());
        }
    }

    // ------------------------------------------------------------------
    @Nested
    @DisplayName("Edge cases")
    class EdgeCaseTests {

        @Test
        void freshInventory_isEmpty() {
            assertTrue(inventory.isEmpty());
            assertNull(inventory.findByIsbn(ISBN_1).orElse(null));
        }

        @Test
        void titleAndAuthorAreTrimmed() {
            inventory.addBook(ISBN_1, "  Effective Java  ", "  Joshua Bloch ", 2018);
            Book book = inventory.getBookByIsbn(ISBN_1);
            assertEquals("Effective Java", book.getTitle());
            assertEquals("Joshua Bloch", book.getAuthor());
        }

        @Test
        void yearBoundaryValues_areAcceptedAtLimitsAndRejectedBeyond() {
            inventory.addBook(ISBN_1, "Oldest", "Gutenberg Press", BookValidator.MIN_YEAR);
            inventory.addBook(ISBN_2, "Newest", "Modern Author", BookValidator.maxYear());
            assertEquals(2, inventory.size());

            assertThrows(InvalidBookException.class,
                    () -> inventory.addBook(ISBN_3, "Too old", "A", BookValidator.MIN_YEAR - 1));
            assertThrows(InvalidBookException.class,
                    () -> inventory.addBook(ISBN_3, "Too new", "A", BookValidator.maxYear() + 1));
            assertEquals(2, inventory.size());
        }

        @Test
        void isbnWithTrailingX_isSupported() {
            inventory.addBook("043942089X", "Harry Potter", "J.K. Rowling", 1999);
            assertTrue(inventory.findByIsbn("043942089x").isPresent());
        }

        @Test
        void veryLongTitle_isAccepted() {
            String longTitle = "T".repeat(1000);
            inventory.addBook(ISBN_1, longTitle, "Author", 2000);
            assertEquals(1000, inventory.getBookByIsbn(ISBN_1).getTitle().length());
        }

        @Test
        void specialCharactersInTitle_arePreserved() {
            inventory.addBook(ISBN_1, "C++ & Java: A \"Guide\"", "O'Brien", 2000);
            assertEquals("C++ & Java: A \"Guide\"", inventory.getBookByIsbn(ISBN_1).getTitle());
        }

        @Test
        void multipleConsecutiveCrudOperations_leaveConsistentState() {
            inventory.addBook(ISBN_1, "A", "Author A", 2001);
            inventory.addBook(ISBN_2, "B", "Author B", 2002);
            inventory.updateTitle(ISBN_1, "A2");
            inventory.deleteBook(ISBN_2);
            inventory.addBook(ISBN_3, "C", "Author C", 2003);
            inventory.updateAuthor(ISBN_3, "Author C2");
            inventory.addBook(ISBN_2, "B again", "Author B2", 2004);

            assertEquals(3, inventory.size());
            assertEquals("A2", inventory.getBookByIsbn(ISBN_1).getTitle());
            assertEquals("Author C2", inventory.getBookByIsbn(ISBN_3).getAuthor());
            assertEquals("B again", inventory.getBookByIsbn(ISBN_2).getTitle());
        }

        @Test
        void failedOperationsDoNotCorruptLaterOperations() {
            inventory.addBook(ISBN_1, "A", "Author A", 2001);
            assertThrows(DuplicateBookException.class,
                    () -> inventory.addBook(ISBN_1, "Dup", "Dup", 2001));
            assertThrows(BookNotFoundException.class, () -> inventory.deleteBook(ISBN_2));
            assertThrows(InvalidBookException.class, () -> inventory.updateTitle(ISBN_1, ""));

            inventory.addBook(ISBN_2, "B", "Author B", 2002);
            assertEquals(2, inventory.size());
            assertEquals("A", inventory.getBookByIsbn(ISBN_1).getTitle());
        }
    }
}
