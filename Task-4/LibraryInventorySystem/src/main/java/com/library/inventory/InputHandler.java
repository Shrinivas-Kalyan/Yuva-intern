package com.library.inventory;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * Reads and converts console input. Keeping this out of the menu class means
 * {@link InventoryApp} only decides <em>what</em> to ask, while this class
 * knows <em>how</em> to read it. Streams are injected so tests can script input.
 */
public class InputHandler {

    private final Scanner scanner;
    private final PrintStream out;

    public InputHandler(InputStream in, PrintStream out) {
        this.scanner = new Scanner(in);
        this.out = out;
    }

    /**
     * Prints the prompt and returns the next line.
     *
     * @throws java.util.NoSuchElementException when the input has ended
     */
    public String readLine(String prompt) {
        out.print(prompt);
        return scanner.nextLine();
    }

    /** Reads a publication year; non-numeric text is reported as invalid book data. */
    public int readYear(String prompt) {
        String text = readLine(prompt).trim();
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw new InvalidBookException("Publication year must be a number: " + text);
        }
    }
}
