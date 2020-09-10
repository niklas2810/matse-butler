package com.niklasarndt.matsebutler.modules.core.command;

import com.niklasarndt.matsebutler.modules.ButlerCommand;
import com.niklasarndt.matsebutler.modules.ButlerContext;
import com.niklasarndt.matsebutler.util.ButlerUtils;

/**
 * Created by Niklas on 2020/07/25.
 */
public class UptimeCommand extends ButlerCommand {

    public UptimeCommand() {
        super("uptime", 0, 0, "Displays the uptime of the bot.");
    }

    @Override
    public void execute(ButlerContext context) {
        long runtime = System.currentTimeMillis() - context.instance().getStartupTimestamp();
        String out = ButlerUtils.prettyPrintTime(runtime);
        context.resultBuilder().success("Uptime: %s.", out);
    }
}
