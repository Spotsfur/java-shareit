package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ExceptionsUnitTest {

    @Test
    void testExceptionsCoverage() {
        ValidationException ex1 = new ValidationException("valid error");
        NotFoundException ex2 = new NotFoundException("not found error");
        ConflictException ex3 = new ConflictException("conflict error");

        assertEquals("valid error", ex1.getMessage());
        assertEquals("not found error", ex2.getMessage());
        assertEquals("conflict error", ex3.getMessage());
    }
}
