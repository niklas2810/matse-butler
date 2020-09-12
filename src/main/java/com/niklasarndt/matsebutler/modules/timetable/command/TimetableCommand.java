package com.niklasarndt.matsebutler.modules.timetable.command;

import com.niklasarndt.matsebutler.modules.ButlerCommand;
import com.niklasarndt.matsebutler.modules.ButlerContext;
import com.niklasarndt.matsebutler.modules.timetable.DateUtils;
import com.niklasarndt.matsebutler.modules.timetable.TimetableBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.internal.utils.tuple.Pair;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class TimetableCommand extends ButlerCommand {

    private final TimetableBuilder builder = new TimetableBuilder();

    public TimetableCommand() {
        super("timetable", 0, 2, "Updates the timetable.",
                false);
    }

    @Override
    public void execute(ButlerContext context) {
        if (context.args().length == 0
                || context.args()[0].equalsIgnoreCase("heute")
                || context.args()[0].equalsIgnoreCase("today")) {

            //Pair<LocalDate, LocalDate> week = DateUtils.getCurrentWeek();
            List<EmbedBuilder> embeds = builder.buildTimetableForToday(context);

            embeds.forEach(embed ->
                    context.message().getChannel().sendMessage(embed.build()).queue());
            return;
        } else if (context.args()[0].equalsIgnoreCase("update")) {
            if (!context.instance().isAdmin(context.message().getAuthor().getIdLong())) {
                context.resultBuilder().denyAccess();
                return;
            }

            if (context.args().length == 1 ||
                    context.args()[1].equalsIgnoreCase("all")) {
                updateChannels(context, true, true);
                return;
            } else if (context.args()[1].equalsIgnoreCase("today")) {
                updateChannels(context, true, false);
                return;
            } else if (context.args()[1].equalsIgnoreCase("weekly")) {
                updateChannels(context, false, true);
                return;
            }
        }


        context.resultBuilder()
                .notFound("Please use `today` or " +
                        "`today update (all|today|weekly)` (Admins only!)");
    }

    private void updateChannels(ButlerContext context, boolean daily, boolean weekly) {
        if (daily) {
            logger.info("Producing daily timetable");
            Optional<GuildChannel> dailyChannel =
                    retrieveChannel(context, context.instance().getConfig().getDailyChannel());

            if (dailyChannel.isEmpty()) {
                context.resultBuilder().error("The channel '%s' does not exist.",
                        context.instance().getConfig().getDailyChannel());
                return;
            }

            MessageChannel out = (MessageChannel) dailyChannel.get();
            clearChannel(out);
            List<EmbedBuilder> embeds = builder.buildTimetableForToday(context);
            embeds.forEach(embed ->
                    out.sendMessage(embed.build()).queue());
            logger.info("Sending {} messages.", embeds.size());
        }

        if (weekly) {
            logger.info("Producing weekly timetable");
            Optional<GuildChannel> weeklyChannel =
                    retrieveChannel(context, context.instance().getConfig().getWeeklyChannel());

            if (weeklyChannel.isEmpty()) {
                context.resultBuilder().error("The channel '%s' does not exist.",
                        context.instance().getConfig().getDailyChannel());
                return;
            }

            MessageChannel out = (MessageChannel) weeklyChannel.get();
            clearChannel(out);
            Pair<LocalDate, LocalDate> week = DateUtils.getCurrentWeek();
            List<EmbedBuilder> embeds = builder.buildTimetable(context,
                    week.getLeft(), week.getRight());
            embeds.forEach(embed ->
                    out.sendMessage(embed.build()).queue());
            logger.info("Sending {} messages.", embeds.size());
        }
    }

    private Optional<GuildChannel> retrieveChannel(ButlerContext context, String name) {
        return context.message().getGuild().getChannels()
                .stream().filter(ch -> ch.getType() == ChannelType.TEXT
                        && ch.getName().equals(name)).findFirst();
    }

    private void clearChannel(MessageChannel out) {
        out.getHistoryFromBeginning(25)
                .queue(hist -> hist.getRetrievedHistory()
                        .forEach(message -> message.delete().queue()));
    }
}
