package com.niklasarndt.matsebutler.modules.timetable;

import java.time.LocalDate;

public class DateUtils {

    public static String getDay(LocalDate d) {
        return String.format("%d-%d-%d", d.getYear(), d.getMonth().getValue(), d.getDayOfMonth());
    }

    public static String getDayRequest() {
        return "https://www.matse.itc.rwth-aachen.de/stundenplan/web/eventFeed/1?" + getCurrentDay();
    }

    public static String getWeekRequest() {
        return "https://www.matse.itc.rwth-aachen.de/stundenplan/web/eventFeed/1?" + getCurrentWeek();
    }

    public static String getCurrentDay() {
        LocalDate d = LocalDate.now();

        String param = getDay(d);
        return String.format("start=%s&end=%s", param, param);
    }

    public static String getCurrentWeek() { //Mo-Fr
        LocalDate d = LocalDate.now();

        return String.format("start=%s&end=%s",
                getDay(d.minusDays(d.getDayOfWeek().ordinal())),
                getDay(d.minusDays(d.getDayOfWeek().ordinal() - 4)));
    }
}
