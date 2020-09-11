package com.niklasarndt.matsebutler.modules.core.command;

import com.niklasarndt.matsebutler.modules.ButlerCommand;
import com.niklasarndt.matsebutler.modules.ButlerContext;

/**
 * Created by Niklas on 2020/07/26.
 */
public class ShutdownCommand extends ButlerCommand {

    public ShutdownCommand() {
        super("shutdown", "Closes the bot application.", true,
                "poweroff", "exit");
    }

    @Override
    public void execute(ButlerContext context) {
        context.resultBuilder().success("The application will terminate in 5 seconds.");
        context.instance().shutdown(0);
    }
}
