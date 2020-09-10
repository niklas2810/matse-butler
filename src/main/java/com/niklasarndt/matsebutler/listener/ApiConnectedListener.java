package com.niklasarndt.matsebutler.listener;

import com.niklasarndt.matsebutler.Butler;
import com.niklasarndt.matsebutler.util.ExecutionFlags;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Nonnull;

/**
 * Created by Niklas on 2020/07/26.
 */
public class ApiConnectedListener extends ListenerAdapter {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Butler butler;

    public ApiConnectedListener(Butler butler) {
        this.butler = butler;
    }

    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        logger.info("Connection to the Discord API is now established. Listening for events!");
        if (butler.hasFlag(ExecutionFlags.NO_MODULE_MANAGER))
            logger.warn("Module manager is disabled.");
    }
}
