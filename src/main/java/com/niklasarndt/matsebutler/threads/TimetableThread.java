package com.niklasarndt.matsebutler.threads;

import com.niklasarndt.matsebutler.Butler;
import com.niklasarndt.matsebutler.enums.ResultType;
import com.niklasarndt.matsebutler.modules.timetable.DateUtils;
import com.niklasarndt.matsebutler.modules.timetable.TimetableBuilder;
import com.niklasarndt.matsebutler.modules.timetable.TimetableModule;
import com.niklasarndt.matsebutler.util.ResultBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.internal.utils.tuple.Pair;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Niklas on 2020/09/13.
 */
public class TimetableThread extends RepeatingThreadScheme {

    private final TimetableModule module;

    private final AtomicInteger dailyHashCode = new AtomicInteger();
    private final AtomicInteger weeklyHashCode = new AtomicInteger();
    private final TimetableBuilder builder = new TimetableBuilder();
    private Butler butler;

    public TimetableThread(TimetableModule module) {
        super("timetable");
        this.module = module;
        super.setInitialDelay(10000);
    }

    @Override
    public void run() {
        this.butler = module.getButler();

        super.setSleepTime(butler.getConfig().getSleepDuration());

        ResultBuilder res = new ResultBuilder(module.info());

        try {
            updateChannels(butler.getJda().getGuildById(butler.getGuild()),
                    res, true, true, false);
        } catch (Exception ex) {
            logger.error("Could not update channels (excpetion occurred)", ex);
        }

        if (res.getType() == ResultType.ERROR) {
            logger.error("Could not update channels{}", res.getOutput());
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

            if (res.getRight().stream()
                    .filter(i -> i.getFields().size() == 1 &&
                            i.getFields().get(0).getValue().equals(TimetableBuilder.NO_LESSONS))
                    .count() == res.getRight().size()) { //All lessons are empty
                logger.info("Sending no timetable entries as there are no lessons scheduled yet.");
                out.sendMessage(TimetableBuilder.NO_LESSONS).queue();
                return;
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
