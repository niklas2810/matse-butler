package com.niklasarndt.matsebutler.modules.timetable;

import net.dv8tion.jda.internal.utils.tuple.Pair;
import java.time.LocalDate;

public class DateUtils {

    public static String getDay(LocalDate d) {
        return String.format("%d-%d-%d", d.getYear(), d.getMonth().getValue(), d.getDayOfMonth());
    }

    public static String getDayRequest(LocalDate day) {
        return getRequest(day, day);
    }

    public static String getRequest(Pair<LocalDate, LocalDate> span) {
        return getRequest(span.getLeft(), span.getRight());
    }

    public static String getRequest(LocalDate start, LocalDate end) {
        return "https://www.matse.itc.rwth-aachen.de/stundenplan/web/eventFeed/1?" +
                getParamsFor(start, end);
    }


    public static LocalDate getCurrentDay() {
        LocalDate d = LocalDate.now();

        if (d.getDayOfWeek().ordinal() > 4) {
            d = d.plusDays(d.getDayOfWeek().ordinal() - 3);
        }
        return d;
    }

    public static Pair<LocalDate, LocalDate> getCurrentWeek() { //Mo-Fr
        LocalDate d = LocalDate.now();

        return new Pair<>() {
            @Override
            public LocalDate getLeft() {
                return d.minusDays(d.getDayOfWeek().ordinal());
            }

            @Override
            public LocalDate getRight() {
                return d.minusDays(d.getDayOfWeek().ordinal() - 4);
            }
        };
    }

    private static String getParamsFor(LocalDate startAndEnd) {
        return getParamsFor(startAndEnd, startAndEnd);
    }

    private static String getParamsFor(Pair<LocalDate, LocalDate> span) {
        return getParamsFor(span.getLeft(), span.getRight());
    }

    public static String getParamsFor(LocalDate start, LocalDate end) {
        return getParamsFor(getDay(start), getDay(end));
    }

    public static String getParamsFor(String start, String end) {
        return String.format("start=%s&end=%s", start, end);
    }
}
