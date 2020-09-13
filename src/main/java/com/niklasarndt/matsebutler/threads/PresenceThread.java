package com.niklasarndt.matsebutler.threads;

import com.niklasarndt.matsebutler.Butler;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;

/**
 * Created by Niklas on 2020/09/13.
 */
public class PresenceThread extends RepeatingThreadScheme {

    private final Butler butler;

    public PresenceThread(Butler butler) {
        super("presence");
        this.butler = butler;
    }

    @Override
    public void run() {
        logger.info("Updating!");
        Guild guild = butler.getJda().getGuildById(butler.getGuild());

        if (guild == null) {
            logger.warn("Can not find main Guild (id: {})!", butler.getGuild());
            return;
        }

        butler.getJda().getPresence() //Set presence to the guild's name
                .setActivity(Activity.of(Activity.ActivityType.WATCHING, guild.getName()));
    }
}
