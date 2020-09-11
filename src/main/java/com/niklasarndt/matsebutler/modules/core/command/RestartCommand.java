package com.niklasarndt.matsebutler.modules.core.command;

import com.niklasarndt.matsebutler.modules.ButlerCommand;
import com.niklasarndt.matsebutler.modules.ButlerContext;

/**
 * Created by Niklas on 2020/07/27.
 */
public class RestartCommand extends ButlerCommand {

    public RestartCommand() {
        super("restart", "Closes the application with an error exit code. " +
                "Docker might react with a restart of the container when configured accordingly.", true);
    }

    @Override
    public void execute(ButlerContext context) {
        context.resultBuilder().success("The application will terminate in 5 seconds.");
        context.instance().shutdown(1);
    }
}
