package com.niklasarndt.matsebutler.modules;

import com.niklasarndt.matsebutler.modules.fake.FakeModule;
import com.niklasarndt.testing.util.ButlerTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import java.util.Optional;

/**
 * Created by Niklas on 2020/07/27.
 */
class ButlerModuleTest extends ButlerTest {

    @Test
    public void testModuleInitialization() {
        ButlerModule module = new FakeModule();
        module.onStartup();
        assertEquals(2, module.getCommandCount());

        Optional<ButlerCommand> cmd = module.getCommand("fake");
        assertTrue(cmd.isPresent());
        assertEquals(cmd.get().info().getName(), "fake");

        module.onShutdown();
    }

}