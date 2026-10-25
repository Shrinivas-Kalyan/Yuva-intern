package com.library.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("BookValidator")
class BookValidatorTest {

    @Test
    void normalizeIsbn_thirteenDigits_isAccepted() {
        assertEquals("9780134685991", BookValidator.normalizeIsbn("9780134685991"));
    }

    @Test
    void normalizeIsbn_tenDigits_isAccepted() {
        assertEquals("0134685997", BookValidator.normalizeIsbn("0134685997"));
    }

    @Test
    void normalizeIsbn_hyphensAndSpacesAreRemoved() {
        assertEquals("9780134685991", BookValidator.normalizeIsbn("978-0-13-468599-1"));
        assertEquals("0134685997", BookValidator.normalizeIsbn(" 0 13 468599 7 "));
    }

    @Test
    void normalizeIsbn_trailingLowercaseXIsAcceptedAndUppercased() {
        assertEquals("043942089X", BookValidator.normalizeIsbn("043942089x"));
    }

    @Test
    void normalizeIsbn_nullOrBlank_throws() {
        assertThrows(InvalidBookException.class, () -> BookValidator.normalizeIsbn(null));
        assertThrows(InvalidBookException.class, () -> BookValidator.normalizeIsbn(""));
        assertThrows(InvalidBookException.class, () -> BookValidator.normalizeIsbn("   "));
    }

    @Test
    void normalizeIsbn_wrongLengthOrCharacters_throws() {
        assertThrows(InvalidBookException.class, () -> BookValidator.normalizeIsbn("12345"));
        assertThrows(InvalidBookException.class, () -> BookValidator.normalizeIsbn("12345678901"));
        assertThrows(InvalidBookException.class, () -> BookValidator.normalizeIsbn("abcdefghij"));
        assertThrows(InvalidBookException.class, () -> BookValidator.normalizeIsbn("97801346859X1"));
    }

    @Test
    void validateTitle_trimsWhitespace() {
        assertEquals("Clean Code", BookValidator.validateTitle("  Clean Code  "));
    }

    @Test
    void validateTitle_nullOrBlank_throws() {
        assertThrows(InvalidBookException.class, () -> BookValidator.validateTitle(null));
        assertThrows(InvalidBookException.class, () -> BookValidator.validateTitle(""));
        assertThrows(InvalidBookException.class, () -> BookValidator.validateTitle("   "));
    }

    @Test
    void validateAuthor_nullOrBlank_throws() {
        assertThrows(InvalidBookException.class, () -> BookValidator.validateAuthor(null));
        assertThrows(InvalidBookException.class, () -> BookValidator.validateAuthor(""));
        assertThrows(InvalidBookException.class, () -> BookValidator.validateAuthor("  "));
    }

    @Test
    void validateYear_lowerBoundary() {
        assertEquals(BookValidator.MIN_YEAR, BookValidator.validateYear(BookValidator.MIN_YEAR));
        assertThrows(InvalidBookException.class,
                () -> BookValidator.validateYear(BookValidator.MIN_YEAR - 1));
    }

    @Test
    void validateYear_upperBoundary_currentYearOkNextYearRejected() {
        int max = BookValidator.maxYear();
        assertEquals(max, BookValidator.validateYear(max));
        assertThrows(InvalidBookException.class, () -> BookValidator.validateYear(max + 1));
    }

    @Test
    void validateYear_zeroAndNegative_throw() {
        assertThrows(InvalidBookException.class, () -> BookValidator.validateYear(0));
        assertThrows(InvalidBookException.class, () -> BookValidator.validateYear(-2000));
    }
}
