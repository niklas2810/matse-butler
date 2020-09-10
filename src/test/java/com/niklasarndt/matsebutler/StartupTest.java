package com.niklasarndt.matsebutler;

import com.niklasarndt.testing.util.ButlerTest;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.Test;

/**
 * Created by Niklas on 2020/07/27.
 */
class StartupTest extends ButlerTest {

    @Test
    public void testMain() {
        assertDoesNotThrow(() -> Butler.main(new String[]{"0", "1"}));
    }
}
