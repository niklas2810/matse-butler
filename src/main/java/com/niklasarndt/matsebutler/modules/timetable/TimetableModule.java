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
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.internal.utils.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class TimetableModule extends ButlerModule {

    private static TimetableModule instance;

    private final AtomicLong lastExecution = new AtomicLong();
    private final AtomicLong lastThreadStartup = new AtomicLong();
    private final AtomicInteger dailyHashCode = new AtomicInteger();
    private final AtomicInteger weeklyHashCode = new AtomicInteger();

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final TimetableBuilder builder = new TimetableBuilder();
    private Thread executorThread;


    public TimetableModule() {
        super(Emojis.HOURGLASS, "timetable", "Stundenplan",
                "Zeigt den Stundenplan an.", "1.0");
        instance = this;
        new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            start();
        }).start();
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

    public void updateChannels(ResultBuilder result,
                               boolean daily, boolean weekly, boolean ignoreCache) {
        updateChannels(butler.getJda().getGuildById(butler.getGuild()),
                result, daily, weekly, ignoreCache);
    }

    public void updateChannels(Guild guild, ResultBuilder result,
                               boolean daily, boolean weekly, boolean ignoreCache) {
        if (daily) {
            logger.info("Generating daily timetable");

            LocalDate today = DateUtils.getCurrentDay();
            buildEmbeds(ignoreCache, dailyHashCode,
                    today, today,
                    guild, butler.getConfig().getDailyChannel(),
                    result);
        }

        if (weekly) {
            logger.info("Producing weekly timetable");

            Pair<LocalDate, LocalDate> week = DateUtils.getCurrentWeek();
            buildEmbeds(ignoreCache, weeklyHashCode,
                    week.getLeft(), week.getRight(),
                    guild, butler.getConfig().getWeeklyChannel(),
                    result);
        }
    }

    private void buildEmbeds(boolean ignoreCache, AtomicInteger hash,
                             LocalDate start, LocalDate end,
                             Guild g, String channelName,
                             ResultBuilder result) {
        Pair<Integer, List<EmbedBuilder>> res = builder.buildTimetable(butler,
                start, end, ignoreCache ? 0 : hash.get());

        if (!ignoreCache && res.getLeft() == hash.get()) {
            logger.debug("Hash code remains the same for {}, skipping update", channelName);
        } else {
            hash.set(res.getLeft());
            logger.info("{}'s Hash is now {}", channelName, res.getLeft());

            Optional<GuildChannel> ch =
                    retrieveChannel(g, channelName);

            if (ch.isEmpty()) {
                result.error("The channel '%s' does not exist.",
                        channelName);
                return;
            }

            MessageChannel out = (MessageChannel) ch.get();
            clearChannel(out);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            String role = "";
            if (butler.getConfig().getRoleName() != null) {
                List<Role> roles = g.getRolesByName(butler.getConfig().getRoleName(),
                        false);
                if (roles.size() == 0) {
                    logger.error("Could not find role {}.", butler.getConfig().getRoleName());
                } else {
                    role = roles.get(0).getAsMention();

                }
            }

            out.sendMessage("Here are the latest updates! " + role).queue();
            res.getRight().forEach(embed ->
                    out.sendMessage(embed.build()).queue());
            logger.info("Sending {} messages.", res.getRight().size());
        }
    }

    private Optional<GuildChannel> retrieveChannel(Guild guild, String name) {
        return guild.getChannels()
                .stream().filter(ch -> ch.getType() == ChannelType.TEXT
                        && ch.getName().equals(name)).findFirst();
    }

    private void clearChannel(MessageChannel out) {
        out.getHistoryFromBeginning(25)
                .queue(hist -> out.purgeMessages(hist.getRetrievedHistory()));
    }
}
