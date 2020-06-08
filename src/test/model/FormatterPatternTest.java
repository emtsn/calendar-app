package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FormatterPatternTest {
    @Test
    void testOrdinal() {
        assertEquals(FormatterPattern.ordinal(1), "1st");
        assertEquals(FormatterPattern.ordinal(2), "2nd");
        assertEquals(FormatterPattern.ordinal(3), "3rd");
        assertEquals(FormatterPattern.ordinal(4), "4th");
        assertEquals(FormatterPattern.ordinal(11), "11th");
        assertEquals(FormatterPattern.ordinal(12), "12th");
        assertEquals(FormatterPattern.ordinal(13), "13th");
        assertEquals(FormatterPattern.ordinal(22), "22nd");
        assertEquals(FormatterPattern.ordinal(31), "31st");
    }
}
