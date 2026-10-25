package com.library.inventory;

import java.time.Year;

/**
 * Central place for all input validation rules so they are written once
 * and can be unit tested in isolation.
 */
public final class BookValidator {

    /** Earliest accepted publication year (roughly the invention of the printing press). */
    public static final int MIN_YEAR = 1450;

    private BookValidator() {
        // utility class
    }

    /** Latest accepted publication year: the current calendar year. */
    public static int maxYear() {
        return Year.now().getValue();
    }

    /**
     * Validates an ISBN and returns it in normalized form (hyphens and spaces removed).
     * A valid ISBN has 10 characters (digits, last may be X) or 13 digits.
     */
    public static String normalizeIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new InvalidBookException("ISBN must not be empty.");
        }
        String cleaned = isbn.replace("-", "").replace(" ", "").toUpperCase();
        boolean valid10 = cleaned.matches("\\d{9}[\\dX]");
        boolean valid13 = cleaned.matches("\\d{13}");
        if (!valid10 && !valid13) {
            throw new InvalidBookException(
                    "ISBN must be 10 or 13 digits (hyphens allowed): " + isbn);
        }
        return cleaned;
    }

    public static String validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new InvalidBookException("Title must not be empty.");
        }
        return title.trim();
    }

    public static String validateAuthor(String author) {
        if (author == null || author.trim().isEmpty()) {
            throw new InvalidBookException("Author must not be empty.");
        }
        return author.trim();
    }

    public static int validateYear(int year) {
        if (year < MIN_YEAR) {
            throw new InvalidBookException(
                    "Publication year must be " + MIN_YEAR + " or later: " + year);
        }
        if (year > maxYear()) {
            throw new InvalidBookException(
                    "Publication year cannot be in the future: " + year);
        }
        return year;
    }
}
