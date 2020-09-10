package com.niklasarndt.matsebutler.util;

import com.niklasarndt.testing.util.ButlerTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

/**
 * Created by Niklas on 2020/07/26.
 */
class ButlerUtilsTest extends ButlerTest {

    @Test
    void prettyPrintTime() {
        assertThrows(IllegalArgumentException.class, () -> ButlerUtils.prettyPrintTime(0));
        assertEquals("01 millisecond", ButlerUtils.prettyPrintTime(1, true));
        assertEquals("01 second", ButlerUtils.prettyPrintTime(1_000));
        assertEquals("01 minute", ButlerUtils.prettyPrintTime(60_000));
        assertEquals("01 hour", ButlerUtils.prettyPrintTime(3_600_000));
        assertEquals("01 day", ButlerUtils.prettyPrintTime(86_400_000));
        assertEquals("01 second, 05 milliseconds",
                ButlerUtils.prettyPrintTime(1_005, true));
        assertEquals("01 minute, 01 second", ButlerUtils.prettyPrintTime(61_000));
        assertEquals("01 minute, 02 seconds", ButlerUtils.prettyPrintTime(62_000));
        assertEquals("02 minutes, 01 second", ButlerUtils.prettyPrintTime(121_000));
        assertEquals("01 day, 05 hours, 35 minutes, 12 seconds",
                ButlerUtils.prettyPrintTime(106_512_000));
    }

    @Test
    void parseTime() {
        assertEquals(1_000, ButlerUtils.parseTimeString("1s"));
        assertEquals(60_000, ButlerUtils.parseTimeString("1m"));
        assertEquals(3_600_000, ButlerUtils.parseTimeString("1h"));
        assertEquals(86_400_000, ButlerUtils.parseTimeString("1d"));
        assertEquals(61_000, ButlerUtils.parseTimeString("1m1s"));
        assertEquals(62_000, ButlerUtils.parseTimeString("1m2s"));
        assertEquals(121_000, ButlerUtils.parseTimeString("2m1s"));
        assertEquals(106_512_000,
                ButlerUtils.parseTimeString("1d5h35m12s"));
    }

    @Test
    void trimString() {
        String text = "Hello, World!";
        assertEquals(".", ButlerUtils.trimString(text, 1));
        assertEquals("...", ButlerUtils.trimString(text, 3));
        assertEquals("H...", ButlerUtils.trimString(text, 4));
        assertEquals("Hello...", ButlerUtils.trimString(text, 8));
        assertEquals("Hello, W...", ButlerUtils.trimString(text, 11));
        assertEquals(text, ButlerUtils.trimString(text, text.length()));
    }

    @Test
    void parseInt() {
        assertEquals(1, ButlerUtils.parseInt("1", -1));
        assertEquals(1, ButlerUtils.parseInt("0001", -1));
        assertEquals(-1, ButlerUtils.parseInt("abc", -1));
        assertEquals(-1, ButlerUtils.parseInt(Integer.MAX_VALUE + "0", -1));
    }
}