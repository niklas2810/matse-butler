package com.niklasarndt.matsebutler.modules.timetable;

import com.niklasarndt.matsebutler.modules.ButlerModule;
import com.niklasarndt.matsebutler.util.Emojis;

public class TimetableModule extends ButlerModule {
    public TimetableModule() {
        super(Emojis.HOURGLASS, "timetable", "Stundenplan",
                "Zeigt den Stundenplan an.", "1.0");
    }
}
