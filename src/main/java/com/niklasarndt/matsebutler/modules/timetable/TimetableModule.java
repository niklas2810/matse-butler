package com.niklasarndt.matsebutler.modules.timetable;

import com.niklasarndt.matsebutler.Butler;
import com.niklasarndt.matsebutler.modules.ButlerModule;
import com.niklasarndt.matsebutler.threads.TimetableThread;
import com.niklasarndt.matsebutler.util.Emojis;

public class TimetableModule extends ButlerModule {

    private static TimetableModule instance;
    private final TimetableThread updater;


    public TimetableModule() {
        super(Emojis.HOURGLASS, "timetable", "Stundenplan",
                "Zeigt den Stundenplan an.", "1.0");
        instance = this;

        this.updater = new TimetableThread(this);
        updater.start();
    }

    public static TimetableModule instance() {
        return instance;
    }

    public TimetableThread getUpdater() {
        return updater;
    }

    public Butler getButler() {
        return butler;
    }
}
