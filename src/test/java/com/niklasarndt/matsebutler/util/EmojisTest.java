package com.niklasarndt.matsebutler.util;


import com.niklasarndt.testing.util.ButlerTest;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * Created by Niklas on 2020/07/26.
 */
public class EmojisTest extends ButlerTest {

    @Test
    public void testEmoji() {
        assertTrue(Emojis.getByUnicode("\u2705").isPresent(), "Could not parse unicode");
        assertTrue(Emojis.isEmoji("\u2705"), "Could not determine whether it's an emoji");
        assertFalse(Emojis.isEmoji("\u2705Test"), "Could not determine whether it's an emoji");
        assertTrue(Emojis.isEmoji(Emojis.TOOLS), "Does not work for larger emojis");
    }
}
