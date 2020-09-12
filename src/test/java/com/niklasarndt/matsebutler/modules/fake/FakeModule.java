package com.niklasarndt.matsebutler.modules.fake;

import com.niklasarndt.matsebutler.Butler;
import com.niklasarndt.matsebutler.ModuleManagerTest;
import com.niklasarndt.matsebutler.modules.ButlerModule;
import com.niklasarndt.matsebutler.util.Emojis;

/**
 * Created by Niklas on 2020/07/27.
 */
public class FakeModule extends ButlerModule {

    public FakeModule() {
        super(Emojis.WASTEBASKET, "fake", null, null, "1");
    }

    @Override
    public void onStartup(Butler butler) {
        super.onStartup(butler);
        ModuleManagerTest.HEARTBEAT = "Hello, World!";
    }

    @Override
    public void onShutdown() {
        super.onShutdown();
        ModuleManagerTest.HEARTBEAT = "Hello, World!";
    }
}
