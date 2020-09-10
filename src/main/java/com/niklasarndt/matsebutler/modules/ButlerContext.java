package com.niklasarndt.matsebutler.modules;

import com.niklasarndt.matsebutler.Butler;
import com.niklasarndt.matsebutler.util.ResultBuilder;
import net.dv8tion.jda.api.entities.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Niklas on 2020/07/25.
 *
 * <p>Using shortened getter method names to improve readability.</p>
 */
public class ButlerContext {

    private static final Logger logger = LoggerFactory.getLogger(ButlerContext.class);

    private final Butler instance;
    private final Message message;
    private final String command;
    private final String[] args;

    private final ResultBuilder result;

    public ButlerContext(Butler instance, Message message, String command,
                         String[] args, ResultBuilder result) {
        this.instance = instance;
        this.message = message;
        this.command = command;
        this.args = args;
        this.result = result;

        if (this.instance == null) {
            logger.warn("The bot instance is not set. Some commands may not work as expected.");
        }
    }

    public Butler instance() {
        return instance;
    }

    public Message message() {
        return message;
    }

    public String command() {
        return command;
    }

    public String[] args() {
        return args;
    }

    public ResultBuilder resultBuilder() {
        return result;
    }
}
