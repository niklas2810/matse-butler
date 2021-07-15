package com.niklasarndt.matsebutler.modules;

import com.niklasarndt.testing.util.ButlerTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

/**
 * Created by Niklas on 2020/07/26.
 */
class ButlerModuleInformationTest extends ButlerTest {

    private final ButlerModuleInformation shortInfo = new ButlerModuleInformation("test",
            "test module");

    @Test
    void generateTitle() {
        assertEquals(ButlerModuleInformation.DEFAULT_EMOJI + " **test** (_test_)",
                shortInfo.generateTitle());
    }

    @Test
    void appliesLimits() {
        assertThrows(NullPointerException.class, () -> new ButlerModuleInformation(null));
        assertThrows(IllegalArgumentException.class, () -> new ButlerModuleInformation(""));
        assertThrows(IllegalArgumentException.class, () -> new ButlerModuleInformation("abc", ""));
        assertThrows(IllegalArgumentException.class, () ->
                new ButlerModuleInformation("abc", "abc",
                        null, null, null));
    }
}