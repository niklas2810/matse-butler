package com.niklasarndt.matsebutler;

import com.niklasarndt.matsebutler.util.ExecutionFlags;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import javax.security.auth.login.LoginException;

/**
 * Created by Niklas on 2020/07/27.
 */
class ButlerTest extends com.niklasarndt.testing.util.ButlerTest {

    @Test
    public void testBuild() {
        long time = System.currentTimeMillis();
        Butler instance;
        try {
            instance = new Butler("1");
        } catch (LoginException e) {
            fail(e);
            return;
        }

        assertTrue(instance.hasFlag(1));
        assertTrue(instance.hasFlag(ExecutionFlags.NO_API_CONNECTION));
        assertEquals(1, instance.getFlags().size());
        assertNull(instance.getJda());
        assertEquals(0, instance.getOwnerId());

        long diff = instance.getStartupTimestamp() - time; //Butler instance should load in 50ms.
        logger.debug("DIFF: {}ms", diff);
        assertTrue(diff < 50,
                String.format("Butler instance loads too slow (%dms)!", diff));
        assertTrue(instance.getModuleManager().getModule("fake").isPresent());
    }
}