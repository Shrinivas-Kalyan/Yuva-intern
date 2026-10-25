package com.library.inventory;

import java.time.Year;
import java.util.regex.Pattern;

/**
 * Central place for all input validation rules so they are written once
 * and can be unit tested in isolation.
 */
public final class BookValidator {

    /** Earliest accepted publication year (roughly the invention of the printing press). */
    public static final int MIN_YEAR = 1450;

    // Compiled once. String.matches(...) would rebuild the same Pattern on every call.
    private static final Pattern ISBN_10 = Pattern.compile("\\d{9}[\\dX]");
    private static final Pattern ISBN_13 = Pattern.compile("\\d{13}");

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
        String cleaned = requireText(isbn, "ISBN").replace("-", "").replace(" ", "").toUpperCase();
        if (!ISBN_10.matcher(cleaned).matches() && !ISBN_13.matcher(cleaned).matches()) {
            throw new InvalidBookException(
                    "ISBN must be 10 or 13 digits (hyphens allowed): " + isbn);
        }
        return cleaned;
    }

    public static String validateTitle(String title) {
        return requireText(title, "Title");
    }

    public static String validateAuthor(String author) {
        return requireText(author, "Author");
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

    /**
     * Shared "must not be null or blank" rule (DRY). Title, author and ISBN
     * used to repeat this check, so a change to the rule or its message had
     * to be made in several places.
     */
    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new InvalidBookException(fieldName + " must not be empty.");
        }
        return value.trim();
    }
}
