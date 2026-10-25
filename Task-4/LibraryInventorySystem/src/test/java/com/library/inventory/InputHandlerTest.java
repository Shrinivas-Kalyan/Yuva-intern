package com.library.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Week 4: tests for the InputHandler class extracted from InventoryApp. */
@DisplayName("InputHandler (console input)")
class InputHandlerTest {

    private final ByteArrayOutputStream captured = new ByteArrayOutputStream();

    private InputHandler handlerFor(String text) {
        return new InputHandler(
                new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)),
                new PrintStream(captured, true, StandardCharsets.UTF_8));
    }

    @Test
    void readLine_printsPromptAndReturnsLine() {
        InputHandler handler = handlerFor("hello\n");

        assertEquals("hello", handler.readLine("Say: "));
        assertEquals("Say: ", captured.toString(StandardCharsets.UTF_8));
    }

    @Test
    void readYear_parsesNumberWithSurroundingSpaces() {
        assertEquals(2018, handlerFor("  2018  \n").readYear("Year: "));
    }

    @Test
    void readYear_nonNumericText_throwsInvalidBookException() {
        InvalidBookException e = assertThrows(InvalidBookException.class,
                () -> handlerFor("twenty\n").readYear("Year: "));
        assertTrue(e.getMessage().contains("must be a number"));
    }

    @Test
    void readYear_emptyLine_throwsInvalidBookException() {
        assertThrows(InvalidBookException.class, () -> handlerFor("\n").readYear("Year: "));
    }

    @Test
    void readLine_endOfInput_throwsNoSuchElementException() {
        assertThrows(NoSuchElementException.class, () -> handlerFor("").readLine("Say: "));
    }
}
