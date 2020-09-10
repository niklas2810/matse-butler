package com.niklasarndt.matsebutler.listener;

import com.niklasarndt.matsebutler.Butler;
import com.niklasarndt.matsebutler.util.ExecutionFlags;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Nonnull;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Niklas on 2020/07/26.
 */
public class ApiListener extends ListenerAdapter {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Butler butler;

    public ApiListener(Butler butler) {
        this.butler = butler;
    }

    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        logger.info("Connection to the Discord API is now established. Listening for events!");
        if (butler.hasFlag(ExecutionFlags.NO_MODULE_MANAGER))
            logger.warn("Module manager is disabled.");


        AtomicInteger left = new AtomicInteger();
        if (event.getJDA().getGuildById(butler.getGuildId()) != null) {
            event.getJDA().getGuilds().forEach(g -> {
                if (g.getIdLong() != butler.getGuildId()) {
                    g.leave().queue();
                    left.incrementAndGet();
                }
            });

            logger.debug("Left {} guilds which were not the primary one.", left.get());
        } else {
            logger.error("The guild you specified in the config via GUILD_ID does not exist");
            System.exit(1);
        }
    }

    @Override
    public void onGuildJoin(@Nonnull GuildJoinEvent event) {
        if (event.getGuild().getIdLong() != butler.getGuildId()) event.getGuild().leave().queue();
    }
}
