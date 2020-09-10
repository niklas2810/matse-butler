package com.niklasarndt.matsebutler.util;

import com.niklasarndt.testing.util.ButlerTest;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 * Created by Niklas on 2020/07/27.
 */
class ExecutionFlagsTest extends ButlerTest {

    @Test
    public void testParsing() {
        assertEquals(ExecutionFlags.NO_API_CONNECTION,
                ExecutionFlags.getFlagById(ExecutionFlags.NO_API_CONNECTION.ordinal()));
        assertArrayEquals(new ExecutionFlags[]{
                        ExecutionFlags.NO_API_CONNECTION,
                        ExecutionFlags.NO_MODULE_MANAGER},
                ExecutionFlags.getFlagsById(new Integer[]{
                        ExecutionFlags.NO_API_CONNECTION.ordinal(),
                        ExecutionFlags.NO_MODULE_MANAGER.ordinal()
                }));
    }

    @Test
    public void testPrinting() {
        assertEquals("None", ExecutionFlags.prettyPrint(new ExecutionFlags[0]));
        assertEquals("NO_API_CONNECTION", ExecutionFlags.prettyPrint(
                new ExecutionFlags[]{ExecutionFlags.NO_API_CONNECTION}));
        assertEquals("NO_API_CONNECTION", ExecutionFlags.prettyPrint(
                new Integer[]{ExecutionFlags.NO_API_CONNECTION.ordinal()}));
    }

}