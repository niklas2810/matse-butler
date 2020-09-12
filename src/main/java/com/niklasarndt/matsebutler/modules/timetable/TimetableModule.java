package com.niklasarndt.matsebutler.modules.timetable;

import com.niklasarndt.matsebutler.enums.ResultType;
import com.niklasarndt.matsebutler.modules.ButlerModule;
import com.niklasarndt.matsebutler.util.Emojis;
import com.niklasarndt.matsebutler.util.ResultBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.internal.utils.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class TimetableModule extends ButlerModule {

    private static TimetableModule instance;
    private final AtomicLong lastExecution = new AtomicLong();
    private final AtomicLong lastThreadStartup = new AtomicLong();
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final TimetableBuilder builder = new TimetableBuilder();
    private Thread executorThread;

    public TimetableModule() {
        super(Emojis.HOURGLASS, "timetable", "Stundenplan",
                "Zeigt den Stundenplan an.", "1.0");
        instance = this;
    }

    public static TimetableModule instance() {
        return instance;
    }

    public long lastExecutionTimestamp() {
        return lastExecution.get();
    }

    public boolean isRunning() {
        return executorThread != null && executorThread.getState() != Thread.State.TERMINATED;
    }

    public void start() {
        executorThread = new Thread(null, this::updateThread,
                "Timetable Updater #" + (int) (Math.random() * 1000), 0);
        executorThread.start();
    }

    public void stop() {
        lastThreadStartup.set(0);
    }

    private void updateThread() {
        long startup = System.currentTimeMillis();
        lastThreadStartup.set(startup);

        while (true) {
            logger.debug("Iteration of timetable update");
            lastExecution.set(System.currentTimeMillis());

            ResultBuilder res = new ResultBuilder(info());

            try {
                updateChannels(butler.getJda().getGuildById(butler.getGuild()),
                        res, true, true, false);
            } catch (Exception ex) {
                logger.error("Could not update channels (excpetion occurred)", ex);
            }

            if (res.getType() == ResultType.ERROR) {
                logger.error("Could not update channels{}", res.getOutput());
            }

            try {
                Thread.sleep(butler.getConfig().getSleepDuration());
            } catch (InterruptedException e) {
                logger.warn("Thread has been interrupted!");
            }
            if (lastThreadStartup.get() != startup) { //Another thread has been started
                logger.info("Shutting down duplicate scheduler thread");
                break;
            }
        }
    }

    public void updateChannels(Guild guild, ResultBuilder result,
                               boolean daily, boolean weekly, boolean ignoreCache) {
        if (daily) {
            logger.info("Producing daily timetable");
            Optional<GuildChannel> dailyChannel =
                    retrieveChannel(guild, butler.getConfig().getDailyChannel());

            if (dailyChannel.isEmpty()) {
                result.error("The channel '%s' does not exist.",
                        butler.getConfig().getDailyChannel());
                return;
            }

            MessageChannel out = (MessageChannel) dailyChannel.get();
            clearChannel(out);
            List<EmbedBuilder> embeds = builder.buildTimetableForToday(butler);
            embeds.forEach(embed ->
                    out.sendMessage(embed.build()).queue());
            logger.info("Sending {} messages.", embeds.size());
        }

        if (weekly) {
            logger.info("Producing weekly timetable");
            Optional<GuildChannel> weeklyChannel =
                    retrieveChannel(guild, butler.getConfig().getWeeklyChannel());

            if (weeklyChannel.isEmpty()) {
                result.error("The channel '%s' does not exist.",
                        butler.getConfig().getDailyChannel());
                return;
            }

            MessageChannel out = (MessageChannel) weeklyChannel.get();
            clearChannel(out);
            Pair<LocalDate, LocalDate> week = DateUtils.getCurrentWeek();
            List<EmbedBuilder> embeds = builder.buildTimetable(butler,
                    week.getLeft(), week.getRight());
            embeds.forEach(embed ->
                    out.sendMessage(embed.build()).queue());
            logger.info("Sending {} messages.", embeds.size());
        }
    }

    private Optional<GuildChannel> retrieveChannel(Guild guild, String name) {
        return guild.getChannels()
                .stream().filter(ch -> ch.getType() == ChannelType.TEXT
                        && ch.getName().equals(name)).findFirst();
    }

    private void clearChannel(MessageChannel out) {
        out.getHistoryFromBeginning(25)
                .queue(hist -> hist.getRetrievedHistory()
                        .forEach(message -> message.delete().queue()));
    }
}
