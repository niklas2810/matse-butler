package com.niklasarndt.matsebutler.modules;

import com.niklasarndt.matsebutler.util.ButlerUtils;
import com.niklasarndt.testing.util.ButlerTest;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * Created by Niklas on 2020/07/26.
 */
class ButlerCommandInformationTest extends ButlerTest {

    private final ButlerCommandInformation shortInfo = new ButlerCommandInformation("abc",
            new String[]{"xyz", "uvw", "opq"}, 0, 0, "def");

    @Test
    void buildShortCommandInfo() {
        assertEquals("`abc             `: `def `",
                ButlerUtils.buildShortCommandInfo(shortInfo, 16, 4));
    }

    @Test
    void hasAlias() {
        assertTrue(shortInfo.hasAlias("xyz"));
        assertTrue(shortInfo.hasAlias("opq"));
    }

    @Test
    void appliesLimits() {
        assertThrows(IllegalArgumentException.class, () -> new ButlerCommandInformation(
                ".".repeat(ButlerCommandInformation.MAX_CMD_LENGTH + 1), "abc"));
        assertThrows(IllegalArgumentException.class, () -> new ButlerCommandInformation(
                "", "abc"
        ));
        assertThrows(IllegalArgumentException.class, () -> new ButlerCommandInformation("abc",
                ".".repeat(ButlerCommandInformation.MAX_DESC_LENGTH + 1)));
        assertThrows(IllegalArgumentException.class, () -> new ButlerCommandInformation("abc",
                ""));

        assertThrows(IllegalArgumentException.class, () -> new ButlerCommandInformation("abc",
                -1, 1, "def"));
        assertThrows(IllegalArgumentException.class, () -> new ButlerCommandInformation("abc",
                1, 0, "def"));

        assertDoesNotThrow(() -> new ButlerCommandInformation("abc", 0, 5, "def"));

    }
}