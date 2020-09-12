package com.niklasarndt.matsebutler.modules.timetable.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.niklasarndt.matsebutler.modules.ButlerCommand;
import com.niklasarndt.matsebutler.modules.ButlerContext;
import com.niklasarndt.matsebutler.modules.timetable.DateUtils;
import com.niklasarndt.matsebutler.modules.timetable.model.TimetableEntry;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class TimetableCommand extends ButlerCommand {

    public TimetableCommand() {
        super("timetable", "Updates the timetable.");
    }

    @Override
    public void execute(ButlerContext context) {
        //Pair<LocalDate, LocalDate> week = DateUtils.getCurrentWeek();
        List<EmbedBuilder> embeds = buildTimetableForToday(context);

        embeds.forEach(embed -> context.message().getChannel().sendMessage(embed.build()).queue());
    }

    private List<EmbedBuilder> buildTimetableForToday(ButlerContext context) {
        LocalDate d = DateUtils.getCurrentDay();
        return buildTimetable(context, d, d);
    }

    private List<EmbedBuilder> buildTimetable(ButlerContext context, LocalDate start, LocalDate end) {
        ObjectMapper mapper = new ObjectMapper();
        List<TimetableEntry> list = new ArrayList<>();

        try {
            list.addAll(Arrays.asList(mapper.readValue(new URL(DateUtils.getRequest(start, end)),
                    TimetableEntry[].class)));
        } catch (IOException e) {
            logger.error("Can not parse timetable url", e);
        }

        list.sort(Comparator.comparing(TimetableEntry::getStartParsed)); //Sort entries by start time
        List<EmbedBuilder> embeds = new ArrayList<>();

        start.datesUntil(end.plusDays(1)).forEach(day -> { //Build embeds
            embeds.add(new EmbedBuilder().setTitle(getDayTitle(day))); //Add title of day

            list.stream() //Add every item of this day to the list
                    .filter(i -> i.getStartParsed().getDayOfMonth() == day.getDayOfMonth())
                    .forEach(item -> embeds.get(embeds.size() - 1).addField(buildField(item)));
            if (embeds.get(embeds.size() - 1).getFields().size() == 0) {
                embeds.get(embeds.size() - 1).addField("",
                        "Für diesen Zeitraum sind keine Stunden angesetzt.", false);
            }
        });

        addFooter(embeds, context.instance().getJda().getSelfUser().getAvatarUrl());
        return embeds;
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
        return String.format("%s, %02d.%02d.%04d", date.getDayOfWeek().name(),
                date.getDayOfMonth(), date.getMonthValue(), date.getYear());
    }
}
