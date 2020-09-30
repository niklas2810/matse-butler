package com.niklasarndt.matsebutler.modules.timetable;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.niklasarndt.matsebutler.Butler;
import com.niklasarndt.matsebutler.modules.timetable.model.TimetableEntry;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.internal.utils.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Niklas on 2020/09/12.
 */
public class TimetableBuilder {

    public static final String NO_LESSONS = "Für diesen Zeitraum sind keine Veranstaltungen angesetzt.";
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public Pair<Integer, List<EmbedBuilder>> buildTimetable(Butler butler, LocalDate start,
                                                            LocalDate end, int currentHash) {
        ObjectMapper mapper = new ObjectMapper();
        List<TimetableEntry> list = new ArrayList<>();

        try {
            list.addAll(Arrays.asList(mapper.readValue(new URL(DateUtils.getRequest(start, end)),
                    TimetableEntry[].class)));
        } catch (IOException e) {
            logger.error("Can not parse timetable url", e);
        }

        list.sort(Comparator.comparing(TimetableEntry::getStartParsed)); //Sort entries by start time

        if (list.hashCode() == currentHash) {
            return new Pair<>() {
                @Override
                public Integer getLeft() {
                    return currentHash;
                }

                @Override
                public List<EmbedBuilder> getRight() {
                    return null;
                }
            };
        }


        List<EmbedBuilder> embeds = new ArrayList<>();

        start.datesUntil(end.plusDays(1)).forEach(day -> { //Build embeds
            embeds.add(new EmbedBuilder().setTitle(getDayTitle(day))); //Add title of day

            list.stream() //Add every item of this day to the list
                    .filter(i -> i.getStartParsed().getDayOfMonth() == day.getDayOfMonth())
                    .forEach(item -> embeds.get(embeds.size() - 1).addField(buildField(item)));
            if (embeds.get(embeds.size() - 1).getFields().size() == 0) {
                embeds.get(embeds.size() - 1).addField("",
                        NO_LESSONS,
                        false);
            }
        });

        addFooter(embeds, butler != null ? butler.getJda().getSelfUser().getAvatarUrl() : null);
        return new Pair<>() {
            @Override
            public Integer getLeft() {
                return list.hashCode();
            }

            @Override
            public List<EmbedBuilder> getRight() {
                return embeds;
            }
        };
    }

    private void addFooter(List<EmbedBuilder> embeds, String iconUrl) {
        LocalDateTime now = LocalDateTime.now();
        embeds.forEach(embed -> embed.setFooter(
                String.format("Generated at %02d.%02d.%04d, %02d:%02d",
                        now.getDayOfMonth(), now.getMonthValue(), now.getYear(),
                        now.getHour(), now.getMinute()),
                iconUrl));

    }

    private MessageEmbed.Field buildField(TimetableEntry entry) {
        //Format: <Title> (hh:mm-hh:mm, <Name>)
        //Content: Info
        String title = String.format("%s (%02d:%02d-%02d:%02d%s)",
                entry.getTitle(),
                entry.getStartParsed().getHour(), entry.getStartParsed().getMinute(),
                entry.getEndParsed().getHour(), entry.getEndParsed().getMinute(),
                entry.getLecturer().getName() != null ? ", " + entry.getLecturer().getName() : "");

        return new MessageEmbed.Field(title,
                entry.getInformation().length() > 0 ? entry.getInformation() :
                        "Keine weiteren Informationen", true);
    }

    private String getDayTitle(LocalDate date) { //Parses a LocalDate to dd.mm.yyyy
        return String.format("%s, %02d.%02d.%04d", getWeekdayName(date.getDayOfWeek()),
                date.getDayOfMonth(), date.getMonthValue(), date.getYear());
    }

    private String getWeekdayName(DayOfWeek day) {
        return switch (day) {
            case MONDAY -> "Montag";
            case TUESDAY -> "Dienstag";
            case WEDNESDAY -> "Mittwoch";
            case THURSDAY -> "Donnerstag";
            case FRIDAY -> "Freitag";
            case SATURDAY -> "Samstag";
            case SUNDAY -> "Sonntag";
        };
    }
}
